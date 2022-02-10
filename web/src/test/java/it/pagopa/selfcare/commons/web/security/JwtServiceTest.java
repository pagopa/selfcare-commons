package it.pagopa.selfcare.commons.web.security;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.DefaultClaims;
import it.pagopa.selfcare.commons.base.TargetEnvironment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@ExtendWith(SystemStubsExtension.class)
class JwtServiceTest {

    @SystemStub
    private EnvironmentVariables environmentVariables;

    @Test
    void getClaims_cannotParseSignature() {
        // given
        String signature = "invalid signature";
        // when
        Executable invalid_signature = () -> new JwtService(signature);
        // then
        Assertions.assertThrows(InvalidKeySpecException.class, invalid_signature);
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
        Executable executable = () -> jwtService.getClaims(jwt);
        // then
        Assertions.assertThrows(SignatureException.class, executable);
    }


    @Test
    void getClaims_nullToken() throws Exception {
        // given
        String jwt = null;
        File file = ResourceUtils.getFile("classpath:certs/pubkey.pem");
        String jwtSigningKey = Files.readString(file.toPath(), Charset.defaultCharset());
        JwtService jwtService = new JwtService(jwtSigningKey);
        // when
        Executable executable = () -> jwtService.getClaims(jwt);
        // then
        Assertions.assertThrows(IllegalArgumentException.class, executable);
    }


    @Test
    void getClaims_logClaims() throws Exception {
        // given
        DefaultClaims claims = new DefaultClaims();
        claims.setId("id");
        String jwt = generateToken(loadPrivateKey(), claims);
        File file = ResourceUtils.getFile("classpath:certs/pubkey.pem");
        String jwtSigningKey = Files.readString(file.toPath(), Charset.defaultCharset());
        JwtService jwtService = new JwtService(jwtSigningKey);
        ListAppender<ILoggingEvent> listAppender = Mockito.spy(new ListAppender<>());
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(listAppender);
        ArgumentCaptor<ILoggingEvent> eventArgumentCaptor = ArgumentCaptor.forClass(ILoggingEvent.class);
        // when
        Claims body = jwtService.getClaims(jwt);
        // then
        Mockito.verify(listAppender, Mockito.atLeastOnce()).doAppend(eventArgumentCaptor.capture());
        Assertions.assertEquals(1, eventArgumentCaptor.getAllValues().stream()
                .filter(iLoggingEvent -> Level.DEBUG.equals(iLoggingEvent.getLevel()))
                .count());
    }


    @Test
    void getClaims_doNotLogClaims() throws Exception {
        // given
        DefaultClaims claims = new DefaultClaims();
        claims.setId("id");
        String jwt = generateToken(loadPrivateKey(), claims);
        File file = ResourceUtils.getFile("classpath:certs/pubkey.pem");
        String jwtSigningKey = Files.readString(file.toPath(), Charset.defaultCharset());
        JwtService jwtService = new JwtService(jwtSigningKey);
        environmentVariables.set("ENV_TARGET", TargetEnvironment.PROD);
        ListAppender<ILoggingEvent> listAppender = Mockito.spy(new ListAppender<>());
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(listAppender);
        ArgumentCaptor<ILoggingEvent> eventArgumentCaptor = ArgumentCaptor.forClass(ILoggingEvent.class);
        // when
        Claims body = jwtService.getClaims(jwt);
        // then
        if (Level.TRACE.equals(((Logger) LoggerFactory.getLogger(JwtService.class)).getEffectiveLevel())) {
            Mockito.verify(listAppender).doAppend(eventArgumentCaptor.capture());
            Assertions.assertEquals(0, eventArgumentCaptor.getAllValues().stream()
                    .filter(iLoggingEvent -> Level.DEBUG.equals(iLoggingEvent.getLevel()))
                    .count());
        } else {
            Mockito.verifyNoInteractions(listAppender);
        }
    }


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
        Claims body = jwtService.getClaims(jwt);
        // then
        Assertions.assertNotNull(body);
        Assertions.assertEquals("id", body.getId());
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