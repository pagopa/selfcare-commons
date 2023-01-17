package it.pagopa.selfcare.commons.utils.crypto.service;

import java.io.File;

/** The {@link org.springframework.stereotype.Service} interface to be used in order to apply Cades digital signature. */
public interface CadesSignService {
    void cadesSign(File pdfFile, File signedPdfFile);
}
