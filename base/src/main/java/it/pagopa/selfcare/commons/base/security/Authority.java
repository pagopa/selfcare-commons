package it.pagopa.selfcare.commons.base.security;

import lombok.Getter;

@Getter
public enum Authority {
    ADMIN("Amministratore"),
    LEGAL("Rappresentante Legale dell'ente"),
    ADMIN_REF("Referente Amministrativo"),
    TECH_REF("Referente Tecnico"),
    REVIEWER("Reviewer PagoPA"),
    USER("Utente qualsiasi autenticato");

    private String value;

    Authority(String value) {
        this.value = value;
    }

}
