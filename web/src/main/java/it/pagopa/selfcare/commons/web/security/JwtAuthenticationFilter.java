package it.pagopa.selfcare.commons.web.security;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String MDC_UID = "uid";
    private static final String CLAIMS_UID = "uid";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public JwtAuthenticationFilter(final AuthenticationManager authenticationManager, final JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);

            if (jwt != null) {
                Optional<Claims> claims = jwtService.getClaims(jwt);

                if (claims.isPresent()) {
                    String uid = claims.get().get(CLAIMS_UID, String.class);

                    if (uid != null) {
                        MDC.put(MDC_UID, uid);
                    }
                    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(uid, jwt);
                    Authentication authentication = authenticationManager.authenticate(authRequest);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);

        } finally {
            MDC.remove(MDC_UID);
        }
    }


    private String parseJwt(HttpServletRequest request) {
        String jwt = null;
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            jwt = headerAuth.substring(7);
        }

        return jwt;
    }

}
