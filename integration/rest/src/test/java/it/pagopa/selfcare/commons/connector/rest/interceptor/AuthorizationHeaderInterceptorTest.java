package it.pagopa.selfcare.commons.connector.rest.interceptor;

import feign.RequestTemplate;
import it.pagopa.selfcare.commons.base.security.SelfCareAuthenticationDetails;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationHeaderInterceptorTest {

    private final AuthorizationHeaderInterceptor interceptor;


    AuthorizationHeaderInterceptorTest() {
        interceptor = new AuthorizationHeaderInterceptor();
    }


    @Test
    void apply_nullAuthentication() {
        // given
        RequestTemplate requestTemplate = new RequestTemplate();

        // when
        interceptor.apply(requestTemplate);

        // then
        Map<String, Collection<String>> headers = requestTemplate.headers();
        assertNull(headers.get(HttpHeaders.AUTHORIZATION));
    }


    @Test
    void apply_notNullAuthentication() {
        // given
        String credentials = "credentials";
        String institutionId = "institutionId";
        TestingAuthenticationToken auth = new TestingAuthenticationToken("principal", credentials);
        auth.setDetails(new SelfCareAuthenticationDetails(institutionId));
        SecurityContextHolder.getContext().setAuthentication(auth);
        RequestTemplate requestTemplate = new RequestTemplate();

        // when
        interceptor.apply(requestTemplate);

        // then
        Map<String, Collection<String>> headers = requestTemplate.headers();
        Collection<String> headerValues = headers.get(HttpHeaders.AUTHORIZATION);
        assertNotNull(headerValues);
        assertEquals(1, headerValues.size());
        Optional<String> headerValue = headerValues.stream().findAny();
        assertTrue(headerValue.isPresent());
        assertEquals("Bearer " + credentials, headerValue.get());
    }
}