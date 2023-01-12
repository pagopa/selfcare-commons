package it.pagopa.selfcare.commons.connector.soap.aruba.sign.service;

import com.sun.xml.ws.developer.JAXWSProperties;
import it.pagopa.selfcare.commons.connector.soap.aruba.sign.config.ArubaSignConfig;
import it.pagopa.selfcare.commons.connector.soap.aruba.sign.generated.client.*;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.xml.ws.BindingProvider;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ArubaSignServiceImpl implements ArubaSignService {

    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final ArubaSignConfig config;

    private final ArubaSignServiceService arubaSignServiceService;

    public ArubaSignServiceImpl(ArubaSignConfig config) {
        this.config = config;

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

        return client;
    }

    @Override
    public OutputStream sign(File pdfFile) {
        it.pagopa.selfcare.commons.connector.soap.aruba.sign.generated.client.ArubaSignService client = getClient();
        SignRequestV2 request = buildSignRequestV2(pdfFile);
        try {
            SignReturnV2 result = client.pdfsignatureV2(request, null, null, null, null, null); // TODO null? possibily it is not the right service
            if("OK".equals(result.getStatus())){
                return result.getStream().getOutputStream();
            } else {
                throw new IllegalStateException(String.format("Something gone wrong while signing input file: %s; obtained the following error: returnCode: %s; description:%s", pdfFile.getName(), result.getReturnCode(), result.getDescription()));
            }
        } catch (TypeOfTransportNotImplemented_Exception | IOException e) {
            throw new IllegalStateException(String.format("Something gone wrong while signing input file: %s", pdfFile.getName()), e);
        }
    }

    private SignRequestV2 buildSignRequestV2(File pdfFile) {
        SignRequestV2 request = new SignRequestV2();

        request.setCertID("AS0");
        request.setProfile("NULL");

        request.setIdentity(config.getAuth());

        request.setTransport(TypeTransport.STREAM);
        request.setStream(new DataHandler(new FileDataSource(pdfFile)));

        request.setRequiredmark(false); // TODO to confirm
        request.setSigningTime(df.format(LocalDateTime.now()));

        return request;
    }
}
