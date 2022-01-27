package it.pagopa.selfcare.commons.web.security;

import io.jsonwebtoken.Claims;
import it.pagopa.selfcare.commons.base.security.SelfCareUser;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Optional;

@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private static final String MDC_UID = "uid";
    private static final String CLAIMS_UID = "uid";
    private static final String CLAIM_EMAIL = "email";

    private final JwtService jwtService;
    private final AuthoritiesRetriever authoritiesRetriever;


    public JwtAuthenticationProvider(JwtService jwtService, AuthoritiesRetriever authoritiesRetriever) {
        this.jwtService = jwtService;
        this.authoritiesRetriever = authoritiesRetriever;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (log.isDebugEnabled()) {
            log.trace("JwtAuthenticationProvider.authenticate");
            log.debug("authentication = {}", authentication);
        }
        final JwtAuthenticationToken requestAuth = (JwtAuthenticationToken) authentication;

        try {
            Claims claims = jwtService.getClaims(requestAuth.getCredentials());
            Optional<String> uid = Optional.ofNullable(claims.get(CLAIMS_UID, String.class));
            uid.ifPresentOrElse(value -> MDC.put(MDC_UID, value),
                    () -> log.warn("uid claims is null"));

            SelfCareUser user = SelfCareUser.builder(uid.orElse("uid_not_provided"))
                    .email(claims.get(CLAIM_EMAIL, String.class))
                    .build();

            JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(requestAuth.getCredentials(),
                    user,
                    authoritiesRetriever.retrieveAuthorities());
            authenticationToken.setDetails(authentication.getDetails());

            return authenticationToken;

        } catch (RuntimeException e) {
            MDC.remove(MDC_UID);
            throw new JwtAuthenticationException(e.getMessage(), e);
        }
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
