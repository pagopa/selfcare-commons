package it.pagopa.selfcare.commons.base.security;

import lombok.Getter;

@Getter
public class SelfCareAuthenticationDetails {

    private final String institutionId;


    public SelfCareAuthenticationDetails(String institutionId) {
        this.institutionId = institutionId;
    }

}
