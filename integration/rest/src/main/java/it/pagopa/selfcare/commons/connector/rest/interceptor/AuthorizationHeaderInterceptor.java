package it.pagopa.selfcare.commons.connector.rest.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class AuthorizationHeaderInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            template.header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", authentication.getCredentials()));

        } else if (RequestContextHolder.getRequestAttributes() != null
                && ServletRequestAttributes.class.isAssignableFrom(RequestContextHolder.getRequestAttributes().getClass())) {
            template.header(HttpHeaders.AUTHORIZATION,
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                            .getRequest()
                            .getHeader(HttpHeaders.AUTHORIZATION));
        }
    }

}
