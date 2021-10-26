package it.pagopa.selfcare.commons.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;

class JwtServiceTest {

    @Test
    void getClaims() throws Exception {
        // given
        DefaultClaims claims = new DefaultClaims();
        claims.setId("id");
        claims.setAudience("audience");
        String jwt = generateToken(loadPrivateKey(), claims);
        System.out.println("jwt = " + jwt);
        JwtService jwtService = new JwtService(jwt, 1000);
        // when
//        jwtService.getClaims(jwt);
        // then

        Claims body = Jwts.parser().setSigningKey(loadPublicKey()).parseClaimsJws(jwt).getBody();
        System.out.println("body = " + body);
    }


    public PublicKey loadPublicKey() throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        FileInputStream inStream = new FileInputStream(ResourceUtils.getFile("classpath:certs/certificate.pem"));
        Certificate cert = cf.generateCertificate(inStream);
        return cert.getPublicKey();

//        File file = ResourceUtils.getFile("classpath:certs/certificate.pem");
//        String key = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
//
////        String publicKeyPEM = key
////                .replace("-----BEGIN PUBLIC KEY-----", "")
////                .replaceAll(System.lineSeparator(), "")
////                .replace("-----END PUBLIC KEY-----", "");
//        String publicKeyPEM = key
//                .replace("-----BEGIN CERTIFICATE-----", "")
//                .replaceAll(System.lineSeparator(), "")
//                .replace("-----END CERTIFICATE-----", "");
//
//        byte[] encoded = Base64.decodeBase64(publicKeyPEM);
//
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
//        return keyFactory.generatePublic(keySpec);
    }


    public PrivateKey loadPrivateKey() throws Exception {
        File file = ResourceUtils.getFile("classpath:certs/key.pem");
        String key = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());

        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.decodeBase64(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return keyFactory.generatePrivate(keySpec);
    }


    private String generateToken(PrivateKey privateKey, Claims claims) {
        String token = null;

        try {
            token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.RS512, privateKey).compact();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return token;
    }


    /**
     * Verify and get claims using public key
     *
     * @param token     the JWT
     * @param publicKey the public key
     * @return the claims extracted from the JWT
     */
    private Claims verifyToken(String token, PublicKey publicKey) {
        Claims claims;

        try {
            claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();

        } catch (Exception e) {
            claims = null;
        }

        return claims;
    }


    /**
     * Generate RSA keys. Uses key size of 2048.
     *
     * @return RSA key pair
     * @throws Exception
     */
    private KeyPair generateRSAKeys() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }
}