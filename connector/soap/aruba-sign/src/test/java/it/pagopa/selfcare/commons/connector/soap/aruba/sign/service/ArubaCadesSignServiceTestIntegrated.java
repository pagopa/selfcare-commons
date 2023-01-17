package it.pagopa.selfcare.commons.connector.soap.aruba.sign.service;

import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.nio.file.Path;

/* Download /secrets/arubaSignServiceSecrets.properties from the following link: TODO */
@TestPropertySource(locations = {
        "classpath:/secrets/arubaSignServiceSecrets.properties",
},
        properties = {
                "aruba.sign-service.baseUrl=https://arss.demo.firma-automatica.it:443/ArubaSignService/ArubaSignService"
        }
)
class ArubaCadesSignServiceTestIntegrated extends ArubaCadesSignServiceTest {

        @Override
        protected File getOutputPadesFile() {
                return Path.of("target/tmp/signedSignTest-Aruba.pdf.p7m").toFile();
        }

}
