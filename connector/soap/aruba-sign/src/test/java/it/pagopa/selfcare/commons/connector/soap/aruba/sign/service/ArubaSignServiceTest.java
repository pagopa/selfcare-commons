package it.pagopa.selfcare.commons.connector.soap.aruba.sign.service;

import it.pagopa.selfcare.commons.connector.soap.aruba.sign.config.ArubaSignConfig;
import it.pagopa.selfcare.commons.connector.soap.aruba.sign.utils.SoapLoggingHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;

import java.nio.file.Path;
import java.util.Base64;

@SpringBootTest(classes = {ArubaSignConfig.class, ArubaSignServiceImpl.class, SoapLoggingHandler.class})
@AutoConfigureWireMock(port = 0, stubs = "classpath:wiremock")
@TestPropertySource(properties = {
        //region wiremock
        "logging.level.WireMock=OFF",
        "logging.level.it.pagopa.selfcare.commons.connector.soap.aruba.sign=DEBUG",
        "aruba.sign-service.baseUrl=http://localhost:${wiremock.server.port}",
        //endregion
})
class ArubaSignServiceTest {

    @Autowired
    private ArubaSignService service;

    @Test
    void test() {
        byte[] result = service.hashSign(Path.of("src/test/resources/signTest.pdf").toFile());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(
                "ivxrc3sxInay9IRpIvR32rJDg6VeVo03xiw/tXHrgR7cXCSIpWPM6V35HVpIdtE5Uc5NljWoL8vbxDhNK4zd4ULEaf7FjTVINAMLcRy0LQAURQqcg2frKLNLdesWwq0q2smxa4vZa87f7XuySSMoyTDvTL/zuPZBo9v8C+COc1oON1NSF7XQhIOc0Cz6i5U+AET2yegqJRvW/RDYJCtLAi2RN3ZKwLy7XqeEbP7LKVYD+EyuMkQPt473uXptEAtVDHUyznBboN6EKDItM3x9Ga/0RUEckx4nc1tQQGY5FwEXtUelSgu/9QoSBD4pfnXkCDEZpzCHHiI+QKbwbHhJUQ==",
                Base64.getEncoder().encodeToString(result)
        );
    }
}
