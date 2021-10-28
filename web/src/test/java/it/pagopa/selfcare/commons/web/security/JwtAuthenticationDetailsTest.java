package it.pagopa.selfcare.commons.web.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

class JwtAuthenticationDetailsTest {

    @Test
    void getInstitutionId() {
        // given
        String headerValue = "institutionId";
        HttpServletRequest requestMock = Mockito.mock(HttpServletRequest.class);
        Mockito.when(requestMock.getHeader(Mockito.any()))
                .thenReturn(headerValue);
        // when
        JwtAuthenticationDetails details = new JwtAuthenticationDetails(requestMock);
        // then
        Assertions.assertEquals(headerValue, details.getInstitutionId());
    }

}