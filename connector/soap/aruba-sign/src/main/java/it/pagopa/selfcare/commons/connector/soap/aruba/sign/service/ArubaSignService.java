package it.pagopa.selfcare.commons.connector.soap.aruba.sign.service;

import java.io.File;
import java.io.InputStream;

/** The {@link org.springframework.stereotype.Service} interface to be used in order to perform hash sign request towards Aruba. */
public interface ArubaSignService {
    byte[] hashSign(InputStream is);
    byte[] pkcs7Signhash(InputStream is);
    void padesSign(File pdfFile, File signedPdfFile);
}