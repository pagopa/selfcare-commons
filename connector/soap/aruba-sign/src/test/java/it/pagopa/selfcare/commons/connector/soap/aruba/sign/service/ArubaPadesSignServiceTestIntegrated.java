package it.pagopa.selfcare.commons.connector.soap.aruba.sign.service;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.OperatorCreationException;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;

/* Download /secrets/arubaSignServiceSecrets.properties from the following link: https://pagopa.atlassian.net/wiki/spaces/SCP/pages/616857618/Firma+digitale+per+mezzo+dei+servizi+di+Aruba */
@TestPropertySource(locations = {
        "classpath:/secrets/arubaSignServiceSecrets.properties",
},
        properties = {
                "aruba.sign-service.baseUrl=https://arss.demo.firma-automatica.it:443/ArubaSignService/ArubaSignService"
        }
)
class ArubaPadesSignServiceTestIntegrated extends ArubaPadesSignServiceTest {

        @Test
        @Override
        protected void testPadesSign() throws IOException, GeneralSecurityException, OperatorCreationException, CMSException {
                verifySignerInformation=true;
                super.testPadesSign();
        }

        @Override
        protected File getOutputPadesFile() {
                return Path.of("target/tmp/signedSignTest-Aruba.pdf").toFile();
        }

}
