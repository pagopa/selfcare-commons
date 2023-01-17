package it.pagopa.selfcare.commons.utils.crypto.service;

import it.pagopa.selfcare.commons.utils.crypto.config.LocalCryptoConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Base64;

class Pkcs7HashSignServiceTest {

    private final Pkcs7HashSignService service = new Pkcs7HashSignServiceImpl(new LocalCryptoConfig());

    @Test
    void test() throws IOException {
        try (FileInputStream fis = new FileInputStream(Path.of("src/test/resources/signTest.pdf").toFile())) {
            byte[] result = service.sign(fis);
            Assertions.assertNotNull(result);
            checkPkcs7HashSign(Base64.getEncoder().encodeToString(result));
        }
    }

    protected void checkPkcs7HashSign(String result) {
        String expectedPrefix="MIAGCSqGSIb3DQEHAqCAMIACAQExDzANBglghkgBZQMEAgEFADCABgkqhkiG9w0BBwEAAKCAMIIDnzCCAoegAwIBAgIUJ8/0z+sR6Llr9FcIGoc5nvZQydgwDQYJKoZIhvcNAQELBQAwXzELMAkGA1UEBhMCSVQxDTALBgNVBAgMBFJPTUUxDTALBgNVBAcMBFJPTUUxDjAMBgNVBAoMBUlEUEFZMQ4wDAYDVQQLDAVJRFBBWTESMBAGA1UEAwwJbG9jYWxob3N0MB4XDTIyMTEwOTE1MTI0NFoXDTMyMDkxNzE1MTI0NFowXzELMAkGA1UEBhMCSVQxDTALBgNVBAgMBFJPTUUxDTALBgNVBAcMBFJPTUUxDjAMBgNVBAoMBUlEUEFZMQ4wDAYDVQQLDAVJRFBBWTESMBAGA1UEAwwJbG9jYWxob3N0MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArDOJKswwCaKdYJbaHZz3bgEIl7z1ArZpNI54ZGaXcRitiwjr/W9fenW69mG7IAlITuPtaIu4iggXTcSRuaulres2EvuP7KjL0tfox/PstqaMZzLF8wOYfJE4iJ8ffcQL67LJ3/Wwn2FhYVV+4D2AYW8QPdRm406HJG7bNKLmdM9AFUQp6zoTvNegyWQyAfH40i72UopltDubcAykD6YgkRctCtKd8h/BRpIRtMn0AGLM/o5qwYu+eCAy8/7Ppj3HzCwHkDOJad/g2pRj4soJdvn5rP6TM4OVtZ7VehxionkaccBPcyDGSrIo5837XYaGv3r7Rn0rCplfxnU4Gtmd5wIDAQABo1MwUTAdBgNVHQ4EFgQUPYfJeHRHwSLmcueB8jUQSHUReVIwHwYDVR0jBBgwFoAUPYfJeHRHwSLmcueB8jUQSHUReVIwDwYDVR0TAQH/BAUwAwEB/zANBgkqhkiG9w0BAQsFAAOCAQEAK34LEHSVM44Wwbs9nKDKeQTRGosdd+gQSrqGf3nI0vkhckuaoYPnuFKi+eo2r+J6xXgqhQfrvhXnYxNEJr9U+9ELBc3IjG6bTUS6HyWhu2PJCeckxQJqonVntl99jmEr4G7QJeDc9oJmC0NJqBmQS/D0tMxChNWpYe1AoGXwqc4S6NTd3x2Z8THzv8duMMn7+1f/VOWe7/Iuuvx5DHN2JFi0lvhMqwglIweGn/qLGB0+r9GM+QlfGuZvUey2x3C0DLQnNIkNKktGjaNjCmpZcd9SIVi6TOPpR+AxlIddYvUXu4GYVXyfDPgzPehaJDiI4WMkIMmYSzhMc/lfuDMGowAAMYICPTCCAjkCAQEwdzBfMQswCQYDVQQGEwJJVDENMAsGA1UECAwEUk9NRTENMAsGA1UEBwwEUk9NRTEOMAwGA1UECgwFSURQQVkxDjAMBgNVBAsMBUlEUEFZMRIwEAYDVQQDDAlsb2NhbGhvc3QCFCfP9M/rEei5a/";
        Assertions.assertTrue(
                result.startsWith(expectedPrefix),
                String.format("Prefix unexpected:\n%s\n%s", result, expectedPrefix)
        );
    }
}
