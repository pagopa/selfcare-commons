package it.pagopa.selfcare.commons.connector.soap.aruba.sign.service;

import com.sun.xml.ws.developer.JAXWSProperties;
import it.pagopa.selfcare.commons.connector.soap.aruba.sign.config.ArubaSignConfig;
import it.pagopa.selfcare.commons.connector.soap.aruba.sign.generated.client.ArubaSignServiceService;
import jakarta.xml.ws.BindingProvider;
import org.springframework.stereotype.Service;

@Service
class ArubaSignServiceImpl implements ArubaSignService {

    private final ArubaSignConfig config;

    private final ArubaSignServiceService arubaSignServiceService;

    ArubaSignServiceImpl(ArubaSignConfig config) {
        this.config = config;

        this.arubaSignServiceService = new ArubaSignServiceService();
    }

    private it.pagopa.selfcare.commons.connector.soap.aruba.sign.generated.client.ArubaSignService getClient(){
        it.pagopa.selfcare.commons.connector.soap.aruba.sign.generated.client.ArubaSignService client = arubaSignServiceService.getArubaSignServicePort();
        ((BindingProvider)client).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, config.getBaseUrl());

        if(config.getConnectTimeoutMs()>0){
            ((BindingProvider)client).getRequestContext().put(JAXWSProperties.CONNECT_TIMEOUT, config.getConnectTimeoutMs());
        }
        if(config.getRequestTimeoutMs()>0){
            ((BindingProvider)client).getRequestContext().put(JAXWSProperties.REQUEST_TIMEOUT, config.getRequestTimeoutMs());
        }

        //TODO is custom SSL cert necessary? what protocol? ((BindingProvider)client).getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, SSLContext.getInstance("TLSv1.3"));

        return client;
    }

    @Override
    public String sign(String hash) {
        return "TODO";
    }
}
