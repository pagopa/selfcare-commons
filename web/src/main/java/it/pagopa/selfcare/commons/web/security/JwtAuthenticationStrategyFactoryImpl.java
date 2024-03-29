package it.pagopa.selfcare.commons.web.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Concrete Factory of {@link JwtAuthenticationStrategy}
 */
@Slf4j
@Service
public class JwtAuthenticationStrategyFactoryImpl implements JwtAuthenticationStrategyFactory {

    private final BeanFactory beanFactory;


    @Autowired
    public JwtAuthenticationStrategyFactoryImpl(final BeanFactory beanFactory) {
        log.trace("Initializing {}", JwtAuthenticationStrategyFactoryImpl.class.getSimpleName());
        this.beanFactory = beanFactory;
    }


    @Override
    public JwtAuthenticationStrategy create(final String jwt) {
        final JwtAuthenticationStrategy bean;
        final String issuer;
        try {
            issuer = Jwts.parser()
                    .parseClaimsJwt(Jwts.parser().isSigned(jwt)
                            ? jwt.substring(0, jwt.lastIndexOf('.') + 1)
                            : jwt)
                    .getBody()
                    .getIssuer();
        } catch (JwtException e) {
            throw new JwtAuthenticationException(e.getMessage());
        }
        switch (issuer) {
            case "SPID":
                bean = beanFactory.getBean(SpidJwtAuthenticationStrategy.class);
                break;
            case "kubernetes/serviceaccount":
                bean = beanFactory.getBean(K8sJwtAuthenticationStrategy.class);
                break;
            default:
                throw new IllegalArgumentException("Unknown issuer");
        }
        return bean;
    }

}
