package it.pagopa.selfcare.commons.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

/**
 * Common helper methods to work with JWT
 */
@Slf4j
@Component
public class JwtService {

    private final PublicKey jwtSigningKey;


    public JwtService(@Value("${jwt.signingKey}") String jwtSigningKey) throws Exception {
        this.jwtSigningKey = getPublicKey(jwtSigningKey);
    }


    public Optional<Claims> getClaims(String token) {
        Optional<Claims> claims = Optional.empty();
        try {
            claims = Optional.of(Jwts.parser().setSigningKey(jwtSigningKey).parseClaimsJws(token).getBody());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return claims;
    }


    private PublicKey getPublicKey(String signingKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyPEM = signingKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.getMimeDecoder().decode(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return keyFactory.generatePublic(keySpec);
    }

}
