package it.pagopa.selfcare.commons.utils.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignatureInformation {
    private String name;
    private String location;
    private String reason;
}
