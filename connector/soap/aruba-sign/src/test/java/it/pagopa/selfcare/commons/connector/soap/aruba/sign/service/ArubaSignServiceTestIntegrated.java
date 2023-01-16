package it.pagopa.selfcare.commons.connector.soap.aruba.sign.service;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(locations = {
        "classpath:/secrets/arubaSignServiceSecrets.properties",
},
        properties = {
                "aruba.sign-service.baseUrl=https://arss.demo.firma-automatica.it:443/ArubaSignService/ArubaSignService"
        }
)
class ArubaSignServiceTestIntegrated extends ArubaSignServiceTest {
}
