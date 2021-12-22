package it.pagopa.selfcare.commons.web.security;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;


    public JwtAuthenticationFilter(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.trace("JwtAuthenticationFilter.doFilterInternal");
        try {

            try {
                JwtAuthenticationToken authRequest = new JwtAuthenticationToken(parseJwt(request));
                Authentication authentication = authenticationManager.authenticate(authRequest);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (RuntimeException e) {
                log.error("Cannot set user authentication", e);
            }

            filterChain.doFilter(request, response);

        } finally {
            SecurityContextHolder.clearContext();
            MDC.clear();
        }
    }


    private String parseJwt(HttpServletRequest request) {
        log.trace("JwtAuthenticationFilter.parseJwt");
        String jwt = null;
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            jwt = headerAuth.substring(7);
        }

        return jwt;
    }

}
