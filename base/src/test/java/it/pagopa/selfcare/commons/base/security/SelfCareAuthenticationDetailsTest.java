package it.pagopa.selfcare.commons.base.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SelfCareAuthenticationDetailsTest {

    @Test
    void getInstitutionId() {
        // given
        String headerValue = "institutionId";
        // when
        SelfCareAuthenticationDetails details = new SelfCareAuthenticationDetails(headerValue);
        // then
        assertEquals(headerValue, details.getInstitutionId());
    }

}