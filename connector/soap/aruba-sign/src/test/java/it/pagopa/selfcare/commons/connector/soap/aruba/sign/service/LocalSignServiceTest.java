package it.pagopa.selfcare.commons.connector.soap.aruba.sign.service;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.io.*;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Collections;
import java.util.Enumeration;

/** To proof Pkcs7 detached into Pades sign behavior using self signed certificates */
@Import(LocalSignServiceTest.SelfSignatureConfig.class)
class LocalSignServiceTest extends ArubaSignServiceTest {

    @TestConfiguration
    public static class SelfSignatureConfig {
        private static PrivateKey privateKey;
        private static Certificate certificate;

        static {
            try {
                char[] password = "idpay".toCharArray();

                KeyStore keystore = KeyStore.getInstance("PKCS12");
                keystore.load(new FileInputStream("src/test/resources/keystore.p12"), password);

                Enumeration<String> aliases = keystore.aliases();
                String alias;
                if (aliases.hasMoreElements()) {
                    alias = aliases.nextElement();
                } else {
                    throw new KeyStoreException("Keystore is empty");
                }
                privateKey = (PrivateKey) keystore.getKey(alias, password);
                Certificate[] certificateChain = keystore.getCertificateChain(alias);
                certificate = certificateChain[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Bean
        public SignatureInterface selfSignature() {
            return is -> {
                try {
                    BouncyCastleProvider BC = new BouncyCastleProvider();
                    Store certStore = new JcaCertStore(Collections.singletonList(certificate));

                    CMSTypedDataInputStream input = new CMSTypedDataInputStream(is);
                    CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
                    ContentSigner sha512Signer = new JcaContentSignerBuilder("SHA256WithRSA").setProvider(BC).build(privateKey);

                    gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
                            new JcaDigestCalculatorProviderBuilder().setProvider(BC).build()).build(sha512Signer, new X509CertificateHolder(certificate.getEncoded())
                    ));
                    gen.addCertificates(certStore);
                    CMSSignedData signedData = gen.generate(input, false);

                    return signedData.getEncoded();
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            };
        }

        class CMSTypedDataInputStream implements CMSTypedData {
            InputStream in;

            public CMSTypedDataInputStream(InputStream is) {
                in = is;
            }

            @Override
            public ASN1ObjectIdentifier getContentType() {
                return PKCSObjectIdentifiers.data;
            }

            @Override
            public Object getContent() {
                return in;
            }

            @Override
            public void write(OutputStream out) throws IOException {
                byte[] buffer = new byte[8 * 1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
            }
        }
    }

    @Override
    void testHashSign() {
    }

    @Override
    protected void checkPkcs7HashSign(String result) {
        String expectedPrefix="MIAGCSqGSIb3DQEHAqCAMIACAQExDzANBglghkgBZQMEAgEFADCABgkqhkiG9w0BBwEAAKCAMIIDnzCCAoegAwIBAgIUJ8/0z+sR6Llr9FcIGoc5nvZQydgwDQYJKoZIhvcNAQELBQAwXzELMAkGA1UEBhMCSVQxDTALBgNVBAgMBFJPTUUxDTALBgNVBAcMBFJPTUUxDjAMBgNVBAoMBUlEUEFZMQ4wDAYDVQQLDAVJRFBBWTESMBAGA1UEAwwJbG9jYWxob3N0MB4XDTIyMTEwOTE1MTI0NFoXDTMyMDkxNzE1MTI0NFowXzELMAkGA1UEBhMCSVQxDTALBgNVBAgMBFJPTUUxDTALBgNVBAcMBFJPTUUxDjAMBgNVBAoMBUlEUEFZMQ4wDAYDVQQLDAVJRFBBWTESMBAGA1UEAwwJbG9jYWxob3N0MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArDOJKswwCaKdYJbaHZz3bgEIl7z1ArZpNI54ZGaXcRitiwjr/W9fenW69mG7IAlITuPtaIu4iggXTcSRuaulres2EvuP7KjL0tfox/PstqaMZzLF8wOYfJE4iJ8ffcQL67LJ3/Wwn2FhYVV+4D2AYW8QPdRm406HJG7bNKLmdM9AFUQp6zoTvNegyWQyAfH40i72UopltDubcAykD6YgkRctCtKd8h/BRpIRtMn0AGLM/o5qwYu+eCAy8/7Ppj3HzCwHkDOJad/g2pRj4soJdvn5rP6TM4OVtZ7VehxionkaccBPcyDGSrIo5837XYaGv3r7Rn0rCplfxnU4Gtmd5wIDAQABo1MwUTAdBgNVHQ4EFgQUPYfJeHRHwSLmcueB8jUQSHUReVIwHwYDVR0jBBgwFoAUPYfJeHRHwSLmcueB8jUQSHUReVIwDwYDVR0TAQH/BAUwAwEB/zANBgkqhkiG9w0BAQsFAAOCAQEAK34LEHSVM44Wwbs9nKDKeQTRGosdd+gQSrqGf3nI0vkhckuaoYPnuFKi+eo2r+J6xXgqhQfrvhXnYxNEJr9U+9ELBc3IjG6bTUS6HyWhu2PJCeckxQJqonVntl99jmEr4G7QJeDc9oJmC0NJqBmQS/D0tMxChNWpYe1AoGXwqc4S6NTd3x2Z8THzv8duMMn7+1f/VOWe7/Iuuvx5DHN2JFi0lvhMqwglIweGn/qLGB0+r9GM+QlfGuZvUey2x3C0DLQnNIkNKktGjaNjCmpZcd9SIVi6TOPpR+AxlIddYvUXu4GYVXyfDPgzPehaJDiI4WMkIMmYSzhMc/lfuDMGowAAMYICPTCCAjkCAQEwdzBfMQswCQYDVQQGEwJJVDENMAsGA1UECAwEUk9NRTENMAsGA1UEBwwEUk9NRTEOMAwGA1UECgwFSURQQVkxDjAMBgNVBAsMBUlEUEFZMRIwEAYDVQQDDAlsb2NhbGhvc3QCFCfP9M/rEei5a/";
        Assertions.assertTrue(
                result.startsWith(expectedPrefix),
                String.format("Prefix unexpected:\n%s\n%s", result, expectedPrefix)
        );
    }

    @Override
    @Test
    void testPadesSign() throws IOException, GeneralSecurityException, OperatorCreationException, CMSException {
        verifySignerInformation=true;
        super.testPadesSign();
    }

    @Override
    protected File getOutputPadesFile() {
        return Path.of("target/tmp/signedSignTest-self.pdf").toFile();
    }

    @Override
    protected void verifyCertificateHolder(X509CertificateHolder certificateHolder) throws IOException, CertificateEncodingException {
        Assertions.assertArrayEquals(SelfSignatureConfig.certificate.getEncoded(), certificateHolder.getEncoded());
    }
}
