package it.pagopa.selfcare.commons.utils.crypto.service;

import it.pagopa.selfcare.commons.utils.crypto.config.LocalCryptoConfig;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateEncodingException;
import java.util.Collections;

/**
 * Implementation of {@link Pkcs7HashSignService} which will use provided private and public keys to perform sign operations
 */
@ConditionalOnBean(LocalCryptoConfig.class)
@Service
public class Pkcs7HashSignServiceImpl implements Pkcs7HashSignService {

    private final CMSSignedDataGenerator cmsSignGenerator;

    public Pkcs7HashSignServiceImpl(LocalCryptoConfig localCryptoConfig) {
        try {
            BouncyCastleProvider bc = new BouncyCastleProvider();
            Store<?> certStore = new JcaCertStore(Collections.singletonList(localCryptoConfig.getCertificate()));

            cmsSignGenerator = new CMSSignedDataGenerator();
            ContentSigner sha512Signer = new JcaContentSignerBuilder("SHA256WithRSA").setProvider(bc).build(localCryptoConfig.getPrivateKey());

            cmsSignGenerator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
                    new JcaDigestCalculatorProviderBuilder().setProvider(bc).build()).build(sha512Signer, new X509CertificateHolder(localCryptoConfig.getCertificate().getEncoded())
            ));
            cmsSignGenerator.addCertificates(certStore);
        } catch (CertificateEncodingException | OperatorCreationException | CMSException | IOException e) {
            throw new IllegalStateException("Something gone wrong while initializing CertStore using provided private and public key", e);
        }
    }

    public byte[] sign(InputStream is) throws IOException {
        try {
            CMSTypedDataInputStream input = new CMSTypedDataInputStream(is);
            CMSSignedData signedData = cmsSignGenerator.generate(input, false);
            return signedData.getEncoded();
        } catch (CMSException e) {
            throw new IllegalArgumentException("Something gone wrong while performing pkcs7 hash sign", e);
        }
    }

    private static class CMSTypedDataInputStream implements CMSTypedData {
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
