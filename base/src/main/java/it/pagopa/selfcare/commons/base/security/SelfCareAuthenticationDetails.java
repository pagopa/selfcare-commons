package it.pagopa.selfcare.commons.base.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class SelfCareAuthenticationDetails {

    private final String institutionId;


    public SelfCareAuthenticationDetails(String institutionId) {
        this.institutionId = institutionId;
    }

}
