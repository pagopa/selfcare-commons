package it.pagopa.selfcare.commons.base.security;

import lombok.Getter;

import static it.pagopa.selfcare.commons.base.security.SelfCareAuthority.ADMIN;
import static it.pagopa.selfcare.commons.base.security.SelfCareAuthority.LIMITED;

@Getter
public enum PartyRole {
    MANAGER(ADMIN),
    DELEGATE(ADMIN),
    SUB_DELEGATE(ADMIN),
    OPERATOR(LIMITED);

    private SelfCareAuthority selfCareAuthority;

    PartyRole(SelfCareAuthority selfCareAuthority) {
        this.selfCareAuthority = selfCareAuthority;
    }

}
