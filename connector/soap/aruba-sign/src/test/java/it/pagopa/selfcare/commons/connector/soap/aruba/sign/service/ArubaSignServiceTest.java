package it.pagopa.selfcare.commons.connector.soap.aruba.sign.service;

import it.pagopa.selfcare.commons.connector.soap.aruba.sign.config.ArubaSignConfig;
import it.pagopa.selfcare.commons.connector.soap.aruba.sign.utils.SoapLoggingHandler;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

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
    // mocked data will not be aligned with timestamp alway updated, thus base test could not successfully sign
    protected boolean verifySignerInformation = false;

    @Test
    void testHashSign() throws IOException {
        try (FileInputStream fis = new FileInputStream(Path.of("src/test/resources/signTest.pdf").toFile())) {
            byte[] result = service.hashSign(fis);
            Assertions.assertNotNull(result);
            checkHashSign(Base64.getEncoder().encodeToString(result));
        }
    }

    protected void checkHashSign(String result) {
        Assertions.assertEquals(
                "ivxrc3sxInay9IRpIvR32rJDg6VeVo03xiw/tXHrgR7cXCSIpWPM6V35HVpIdtE5Uc5NljWoL8vbxDhNK4zd4ULEaf7FjTVINAMLcRy0LQAURQqcg2frKLNLdesWwq0q2smxa4vZa87f7XuySSMoyTDvTL/zuPZBo9v8C+COc1oON1NSF7XQhIOc0Cz6i5U+AET2yegqJRvW/RDYJCtLAi2RN3ZKwLy7XqeEbP7LKVYD+EyuMkQPt473uXptEAtVDHUyznBboN6EKDItM3x9Ga/0RUEckx4nc1tQQGY5FwEXtUelSgu/9QoSBD4pfnXkCDEZpzCHHiI+QKbwbHhJUQ==",
                result
        );
    }

    @Test
    void testPkcs7HashSign() throws IOException {
        try (FileInputStream fis = new FileInputStream(Path.of("src/test/resources/signTest.pdf").toFile())) {
            byte[] result = service.pkcs7Signhash(fis);
            Assertions.assertNotNull(result);
            checkPkcs7HashSign(Base64.getEncoder().encodeToString(result));
        }
    }

    protected void checkPkcs7HashSign(String result) {
        Assertions.assertEquals(
                "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",
                result
        );
    }

    @Test
    void testPadesSign() throws IOException, GeneralSecurityException, OperatorCreationException, CMSException {
        File inputFile = Path.of("src/test/resources/signTest.pdf").toFile();
        File outputFile = getOutputPadesFile();
        if (outputFile.exists()) {
            Assertions.assertTrue(outputFile.delete());
        }

        service.padesSign(inputFile, outputFile);
        Assertions.assertTrue(outputFile.exists());

        checkPadesSignature(inputFile, outputFile);
    }

    protected File getOutputPadesFile() {
        return Path.of("target/tmp/signedSignTest-mock.pdf").toFile();
    }

    @SuppressWarnings("unchecked")
    private void checkPadesSignature(File origFile, File signedFile)
            throws IOException, CMSException, OperatorCreationException, GeneralSecurityException
    {
        PDDocument document = PDDocument.load(origFile);
        // get string representation of pages COSObject
        String origPageKey = document.getDocumentCatalog().getCOSObject().getItem(COSName.PAGES).toString();
        document.close();

        document = PDDocument.load(signedFile);

        // early detection of problems in the page structure
        int p = 0;
        PDPageTree pageTree = document.getPages();
        for (PDPage page : document.getPages())
        {
            Assertions.assertEquals(p, pageTree.indexOf(page));
            ++p;
        }

        Assertions.assertEquals(origPageKey, document.getDocumentCatalog().getCOSObject().getItem(COSName.PAGES).toString());

        List<PDSignature> signatureDictionaries = document.getSignatureDictionaries();
        if (signatureDictionaries.isEmpty())
        {
            Assertions.fail("no signature found");
        }
        for (PDSignature sig : document.getSignatureDictionaries())
        {
            byte[] contents = sig.getContents();

            byte[] buf = sig.getSignedContent(new FileInputStream(signedFile));

            // verify that getSignedContent() brings the same content
            // regardless whether from an InputStream or from a byte array
            FileInputStream fis2 = new FileInputStream(signedFile);
            byte[] buf2 = sig.getSignedContent(IOUtils.toByteArray(fis2));
            Assertions.assertArrayEquals(buf, buf2);
            fis2.close();

            // verify that all getContents() methods returns the same content
            FileInputStream fis3 = new FileInputStream(signedFile);
            byte[] contents2 = sig.getContents(IOUtils.toByteArray(fis3));
            Assertions.assertArrayEquals(contents, contents2);
            fis3.close();
            byte[] contents3 = sig.getContents(new FileInputStream(signedFile));
            Assertions.assertArrayEquals(contents, contents3);

            CMSSignedData signedData = new CMSSignedData(new CMSProcessableByteArray(buf), contents);
            Store<?> certificatesStore = signedData.getCertificates();
            Collection<SignerInformation> signers = signedData.getSignerInfos().getSigners();
            SignerInformation signerInformation = signers.iterator().next();
            Collection<?> matches = certificatesStore.getMatches(signerInformation.getSID());
            X509CertificateHolder certificateHolder = (X509CertificateHolder) matches.iterator().next();
            verifyCertificateHolder(certificateHolder);
            if(verifySignerInformation){
                verifySignerInformation(signerInformation, certificateHolder);
            }
        }
        document.close();
    }

    protected void verifyCertificateHolder(X509CertificateHolder certificateHolder) throws IOException, CertificateEncodingException {
        Assertions.assertEquals(
                "C=IT,L=Arezzo,O=ArubaPEC S.p.A.,organizationIdentifier=VATIT-01879020517,OU=Qualified Trust Service Provider,CN=ArubaPEC EU Qualified Certificates CA G1",
                certificateHolder.getIssuer().toString()
        );
    }

    protected void verifySignerInformation(SignerInformation signerInformation, X509CertificateHolder certificateHolder)
            throws CertificateException, OperatorCreationException, CMSException {
        // CMSVerifierCertificateNotValidException means that the keystore wasn't valid at signing time
        if (!signerInformation.verify(new JcaSimpleSignerInfoVerifierBuilder().build(certificateHolder)))
        {
            Assertions.fail("Signature verification failed");
        }
    }
}
