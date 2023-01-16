package it.pagopa.selfcare.commons.connector.soap.aruba.sign.service;

import com.sun.xml.ws.developer.JAXWSProperties;
import it.pagopa.selfcare.commons.connector.soap.aruba.sign.config.ArubaSignConfig;
import it.pagopa.selfcare.commons.connector.soap.aruba.sign.generated.client.ArubaSignServiceService;
import it.pagopa.selfcare.commons.connector.soap.aruba.sign.generated.client.SignHashRequest;
import it.pagopa.selfcare.commons.connector.soap.aruba.sign.generated.client.SignHashReturn;
import it.pagopa.selfcare.commons.connector.soap.aruba.sign.utils.SoapLoggingHandler;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.handler.Handler;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class ArubaSignServiceImpl implements ArubaSignService {

    private final ArubaSignConfig config;
    private final SoapLoggingHandler soapLoggingHandler;

    private final ArubaSignServiceService arubaSignServiceService;


    public ArubaSignServiceImpl(ArubaSignConfig config, SoapLoggingHandler soapLoggingHandler) {
        this.config = config;
        this.soapLoggingHandler = soapLoggingHandler;

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
    public byte[] hashSign(File pdfFile) {
        it.pagopa.selfcare.commons.connector.soap.aruba.sign.generated.client.ArubaSignService client = getClient();
        SignHashRequest request = buildSignHashRequest(pdfFile);
        SignHashReturn result = client.signhash(request);
        if ("OK".equals(result.getStatus())) {
            return result.getSignature();
        } else {
            throw new IllegalStateException(String.format("Something gone wrong while signing input file: %s; obtained the following error: returnCode: %s; description:%s", pdfFile.getName(), result.getReturnCode(), result.getDescription()));
        }
    }

    private SignHashRequest buildSignHashRequest(File pdfFile) {
        SignHashRequest request = new SignHashRequest();

        request.setCertID("AS0");
        request.setIdentity(config.getAuth());

        request.setHash(getDigest(pdfFile));
        request.setHashtype("SHA256");

        request.setRequirecert(false);

        return request;
    }

    private byte[] getDigest(File pdfFile) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(Files.readAllBytes(pdfFile.toPath()));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(String.format("Something gone wrong while calculating digest for input file: %s", pdfFile.getName()), e);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Something gone wrong while reading pdfFile %s", pdfFile), e);
        }
    }
}
