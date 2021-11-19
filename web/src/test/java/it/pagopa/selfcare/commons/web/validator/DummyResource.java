package it.pagopa.selfcare.commons.web.validator;

import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
public class DummyResource {

    @NotNull
    private String dummy;

}
