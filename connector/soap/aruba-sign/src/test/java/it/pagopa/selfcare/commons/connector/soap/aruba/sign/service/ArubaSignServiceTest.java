package it.pagopa.selfcare.commons.connector.soap.aruba.sign.service;

import it.pagopa.selfcare.commons.connector.soap.aruba.sign.config.ArubaSignConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

@SpringBootTest(classes = {ArubaSignConfig.class, ArubaSignServiceImpl.class})
@AutoConfigureWireMock(port = 0, stubs = "classpath:wiremock")
@TestPropertySource(properties = {
        //region wiremock
        "logging.level.WireMock=OFF",
        "aruba.sign-service.baseUrl=http://localhost:${wiremock.server.port}",
        //endregion
})
class ArubaSignServiceTest {

    @Autowired
    private ArubaSignService service;

    @Test
    void test() throws IOException {
        try(OutputStream out = service.sign(Path.of("src/test/resources/signTest.pdf").toFile())){
            Assertions.assertNotNull(out);
        }
    }
}
