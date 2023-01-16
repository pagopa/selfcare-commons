package it.pagopa.selfcare.commons.connector.soap.aruba.sign.service;

import com.sun.xml.ws.developer.JAXWSProperties;
import it.pagopa.selfcare.commons.connector.soap.aruba.sign.config.ArubaSignConfig;
import it.pagopa.selfcare.commons.connector.soap.aruba.sign.generated.client.*;
import it.pagopa.selfcare.commons.connector.soap.aruba.sign.utils.SoapLoggingHandler;
import it.pagopa.selfcare.commons.connector.soap.aruba.sign.utils.Utils;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.handler.Handler;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Service
public class ArubaSignServiceImpl implements ArubaSignService {

    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final ArubaSignConfig config;
    private final SoapLoggingHandler soapLoggingHandler;

    private final ArubaSignServiceService arubaSignServiceService;
    private final SignatureInterface pkcs7Signature;

    public ArubaSignServiceImpl(ArubaSignConfig config, SoapLoggingHandler soapLoggingHandler, @Autowired(required = false) SignatureInterface pkcs7Signature) {
        this.config = config;
        this.soapLoggingHandler = soapLoggingHandler;
        this.pkcs7Signature = Objects.requireNonNullElseGet(pkcs7Signature, () -> this::arubaPkcs7Signhash);

        this.arubaSignServiceService = new ArubaSignServiceService();
    }

    private it.pagopa.selfcare.commons.connector.soap.aruba.sign.generated.client.ArubaSignService getClient() {
        it.pagopa.selfcare.commons.connector.soap.aruba.sign.generated.client.ArubaSignService client = arubaSignServiceService.getArubaSignServicePort();
        ((BindingProvider) client).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, config.getBaseUrl());

        if (config.getConnectTimeoutMs() > 0) {
            ((BindingProvider) client).getRequestContext().put(JAXWSProperties.CONNECT_TIMEOUT, config.getConnectTimeoutMs());
        }
        if (config.getRequestTimeoutMs() > 0) {
            ((BindingProvider) client).getRequestContext().put(JAXWSProperties.REQUEST_TIMEOUT, config.getRequestTimeoutMs());
        }

        //TODO is custom SSL cert necessary? what protocol? ((BindingProvider)client).getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, SSLContext.getInstance("TLSv1.3"));

        @SuppressWarnings("rawtypes")
        List<Handler> handlerChain = ((BindingProvider) client).getBinding().getHandlerChain();
        handlerChain.add(soapLoggingHandler);
        ((BindingProvider) client).getBinding().setHandlerChain(handlerChain);

        return client;
    }

    @Override
    public byte[] hashSign(InputStream is) {
        it.pagopa.selfcare.commons.connector.soap.aruba.sign.generated.client.ArubaSignService client = getClient();
        SignHashRequest request = buildSignHashRequest(is);
        SignHashReturn result = client.signhash(request);
        if ("OK".equals(result.getStatus())) {
            return result.getSignature();
        } else {
            throw new IllegalStateException(String.format("Something gone wrong while signing input file: returnCode: %s; description:%s", result.getReturnCode(), result.getDescription()));
        }
    }

    private SignHashRequest buildSignHashRequest(InputStream is) {
        SignHashRequest request = new SignHashRequest();

        request.setCertID("AS0");
        request.setIdentity(config.getAuth());

        request.setHash(Utils.getDigest(is));
        request.setHashtype("SHA256");

        request.setRequirecert(false);

        return request;
    }

    @Override
    public byte[] pkcs7Signhash(InputStream is) {
        try {
            return pkcs7Signature.sign(is);
        } catch (IOException e) {
            throw new IllegalStateException("Something gone wrong while reading input stream to pkcs7 hash sign", e);
        }
    }

    private byte[] arubaPkcs7Signhash(InputStream is){
        it.pagopa.selfcare.commons.connector.soap.aruba.sign.generated.client.ArubaSignService client = getClient();
        SignRequestV2 request = buildSignRequest(is);
        try {
            SignReturnV2 result = client.pkcs7Signhash(request, false, false);
            if ("OK".equals(result.getStatus())) {
                return result.getBinaryoutput();
            } else {
                throw new IllegalStateException(String.format("Something gone wrong while performing pkcs7 hash sign request: returnCode: %s; description:%s", result.getReturnCode(), result.getDescription()));
            }
        } catch (TypeOfTransportNotImplemented_Exception e) {
            throw new IllegalStateException("Something gone wrong when invoking Aruba in order to calculate pkcs7 hash sign request", e);
        }
    }

    private SignRequestV2 buildSignRequest(InputStream is) {
        SignRequestV2 request = new SignRequestV2();

        request.setCertID("AS0");
        request.setProfile("NULL");
        request.setIdentity(config.getAuth());

        request.setTransport(TypeTransport.BYNARYNET);
        request.setBinaryinput(Utils.getDigest(is));

        request.setRequiredmark(false);
        request.setSigningTime(df.format(LocalDateTime.now()));

        return request;
    }

    @Override
    public void padesSign(File pdfFile, File signedPdfFile) {
        Path destDir=signedPdfFile.toPath().getParent();
        if(!Files.exists(destDir)){
            try {
                Files.createDirectories(destDir);
            } catch (IOException e) {
                throw new IllegalArgumentException(String.format("Something gone wrong while creating destination folder: %s", destDir), e);
            }
        }

        try (
                FileOutputStream fos = new FileOutputStream(signedPdfFile);
                PDDocument doc = PDDocument.load(pdfFile)) {
            // create signature dictionary
            PDSignature signature = new PDSignature();
            signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
            signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
            signature.setName("Example User");
            signature.setLocation("Los Angeles, CA");
            signature.setReason("Testing");

            // the signing date, needed for valid signature
            signature.setSignDate(Calendar.getInstance());

            SignatureOptions signatureOptions = new SignatureOptions();
            // Size can vary, but should be enough for purpose.
            signatureOptions.setPreferredSignatureSize(SignatureOptions.DEFAULT_SIGNATURE_SIZE * 2);
            // register signature dictionary and sign interface
            doc.addSignature(signature, pkcs7Signature, signatureOptions);

            // write incremental (only for signing purpose)
            doc.saveIncremental(fos);

        } catch (Exception e) {
            throw new IllegalStateException(String.format("Something gone wrong while signing input pdf %s and storing it into %s", pdfFile.getAbsolutePath(), signedPdfFile.getAbsolutePath()), e);
        }
    }

}
