package it.pagopa.selfcare.commons.connector.soap.aruba.sign.service;

import java.io.File;
import java.io.OutputStream;

/** The {@link org.springframework.stereotype.Service} interface to be used in order to perform sign request towards Aruba. */
public interface ArubaSignService {
    OutputStream sign(File pdfFile);
}
