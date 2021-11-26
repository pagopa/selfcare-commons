package it.pagopa.selfcare.commons.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

class JwtServiceTest {

    @Test
    void getClaims_signatureOK() throws Exception {
        // given
        DefaultClaims claims = new DefaultClaims();
        claims.setId("id");
        String jwt = generateToken(loadPrivateKey(), claims);
        File file = ResourceUtils.getFile("classpath:certs/pubkey.pem");
        String jwtSigningKey = Files.readString(file.toPath(), Charset.defaultCharset());
        JwtService jwtService = new JwtService(jwtSigningKey);
        // when
        Optional<Claims> body = jwtService.getClaims(jwt);
        // then
        Assertions.assertTrue(body.isPresent());
        Assertions.assertEquals("id", body.get().getId());
    }


    @Test
    void getClaims_signatureNotValid() throws Exception {
        // given
        DefaultClaims claims = new DefaultClaims();
        claims.setId("id");
        String jwt = generateToken(loadPrivateKey(), claims);
        File file = ResourceUtils.getFile("classpath:certs/different_pubkey.pem");
        String jwtSigningKey = Files.readString(file.toPath(), Charset.defaultCharset());
        JwtService jwtService = new JwtService(jwtSigningKey);
        // when
        Optional<Claims> body = jwtService.getClaims(jwt);
        // then
        Assertions.assertFalse(body.isPresent());
    }


    @Test
    void getClaims_cannotParseSignature() throws Exception {
        // given
        DefaultClaims claims = new DefaultClaims();
        claims.setId("id");
        String jwt = generateToken(loadPrivateKey(), claims);
        // when - then
        Assertions.assertThrows(InvalidKeySpecException.class, () -> new JwtService("invalid signature"));
    }


    private PrivateKey loadPrivateKey() throws Exception {
        File file = ResourceUtils.getFile("classpath:certs/key.pem");
        String key = Files.readString(file.toPath(), Charset.defaultCharset());

        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.getMimeDecoder().decode(privateKeyPEM);

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

}