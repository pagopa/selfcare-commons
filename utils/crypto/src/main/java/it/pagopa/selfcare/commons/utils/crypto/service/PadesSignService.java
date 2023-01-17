package it.pagopa.selfcare.commons.utils.crypto.service;

import it.pagopa.selfcare.commons.utils.crypto.model.SignatureInformation;

import java.io.File;

/** The {@link org.springframework.stereotype.Service} interface to be used in order to apply Pades digital signature. */
public interface PadesSignService {
    void padesSign(File pdfFile, File signedPdfFile, SignatureInformation signInfo);
}
