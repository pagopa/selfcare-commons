package it.pagopa.selfcare.commons.utils.crypto.config;

import lombok.Getter;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Enumeration;

/** {@link Configuration} class to read private and public key to use when performing crypto operations */
@Conditional(LocalCryptoCondition.class)
@Configuration
@Getter
public class LocalCryptoConfig {

    private final PrivateKey privateKey;
    private final Certificate certificate;

    public LocalCryptoConfig() {
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
            throw new IllegalStateException("Something gone wrong while loading crypto private and public keys", e);
        }
    }
}
