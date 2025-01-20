package it.pagopa.selfcare.commons.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.commons.web.model.Problem;
import it.pagopa.selfcare.commons.web.security.JwtAuthenticationFilter;
import it.pagopa.selfcare.commons.web.security.JwtAuthenticationProvider;
import it.pagopa.selfcare.commons.web.security.JwtAuthenticationStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@PropertySource("classpath:config/jwt.properties")
@ComponentScan(basePackages = "it.pagopa.selfcare.commons.web.security")
@Import({BaseWebConfig.class, K8sAuthenticationConfig.class})
public class SecurityConfig {

  private static final String[] AUTH_WHITELIST = {
    "/swagger-resources/**",
    "/v3/api-docs",
    "/v3/api-docs/**",
    "/swagger-ui.html",
    "/swagger-ui/**",
    "/favicon.ico",
    "/error",
    "/actuator/**"
  };

  private final JwtAuthenticationStrategyFactory jwtAuthenticationStrategyFactory;
  private final ObjectMapper objectMapper;

  public SecurityConfig(JwtAuthenticationStrategyFactory jwtAuthenticationStrategyFactory, ObjectMapper objectMapper) {
    this.jwtAuthenticationStrategyFactory = jwtAuthenticationStrategyFactory;
    this.objectMapper = objectMapper;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> {
        /*for (String endpoint : AUTH_WHITELIST) {
          authorize.antMatchers(endpoint).permitAll();
        }*/
        authorize.anyRequest().authenticated();
      })
      .exceptionHandling(exceptionHandling -> exceptionHandling
        .accessDeniedHandler((request, response, accessDeniedException) -> {
          log.warn("{} to resource {}", accessDeniedException.getMessage(), request.getRequestURI());
          response.setStatus(HttpStatus.FORBIDDEN.value());
          response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
          final Problem problem = new Problem(HttpStatus.FORBIDDEN, accessDeniedException.getMessage());
          response.getOutputStream().print(objectMapper.writeValueAsString(problem));
        })
        .authenticationEntryPoint((request, response, authException) -> {
          log.warn("{} {}", authException.getMessage(), request.getRequestURI());
          response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"selfcare\"");
          response.setStatus(HttpStatus.UNAUTHORIZED.value());
          response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
          final Problem problem = new Problem(HttpStatus.UNAUTHORIZED, authException.getMessage());
          response.getOutputStream().print(objectMapper.writeValueAsString(problem));
        })
      )
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .cors().and()
      .csrf().disable()
      .formLogin().disable()
      .logout().disable()
      .httpBasic().disable()
      .anonymous().disable()
      .rememberMe().disable()
      .x509().disable()
      .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)), objectMapper),
        UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  /**@Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(jwtAuthenticationStrategyFactory);
    return http.getSharedObject(AuthenticationManagerBuilder.class)
      .authenticationProvider(authenticationProvider)
      .eraseCredentials(false).build();
  }*/

}
