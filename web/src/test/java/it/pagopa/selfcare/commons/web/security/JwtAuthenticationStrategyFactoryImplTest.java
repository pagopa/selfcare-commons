package it.pagopa.selfcare.commons.web.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtAuthenticationStrategyFactoryImplTest {

    private final JwtAuthenticationStrategyFactoryImpl jwtAuthenticationStrategyFactory;
    private final BeanFactory beanFactoryMock;


    public JwtAuthenticationStrategyFactoryImplTest() {
        beanFactoryMock = mock(BeanFactory.class);
        Mockito.when(beanFactoryMock.getBean(any(Class.class)))
                .thenAnswer(invocation -> {
                    final JwtAuthenticationStrategy jwtAuthenticationStrategy;
                    final Class<?> argument = invocation.getArgument(0, Class.class);
                    if (SpidJwtAuthenticationStrategy.class.equals(argument)) {
                        jwtAuthenticationStrategy = mock(SpidJwtAuthenticationStrategy.class);
                    } else if (K8sJwtAuthenticationStrategy.class.equals(argument)) {
                        jwtAuthenticationStrategy = mock(K8sJwtAuthenticationStrategy.class);
                    } else {
                        jwtAuthenticationStrategy = null;
                    }
                    return jwtAuthenticationStrategy;
                });
        jwtAuthenticationStrategyFactory = new JwtAuthenticationStrategyFactoryImpl(beanFactoryMock);
    }


    @Test
    void create_JwtException() {
        // given
        final String jwt = null;
        // when
        final Executable executable = () -> jwtAuthenticationStrategyFactory.create(jwt);
        // then
        assertThrows(JwtAuthenticationException.class, executable);
        verifyNoInteractions(beanFactoryMock);
    }


    @Test
    void create_UnknownIssuer() throws Exception {
        // given
        final DefaultClaims claims = new DefaultClaims();
        claims.setIssuer("invalid issuer");
        final String jwt = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.RS512, loadPrivateKey()).compact();
        // when
        final Executable executable = () -> jwtAuthenticationStrategyFactory.create(jwt);
        // then
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("Unknown issuer", exception.getMessage());
        verifyNoInteractions(beanFactoryMock);
    }


    private static Stream<Arguments> getJwtAuthenticationStrategyArgumentsProvider() {
        return Stream.of(
                Arguments.of(SpidJwtAuthenticationStrategy.class, "SPID"),
                Arguments.of(K8sJwtAuthenticationStrategy.class, "kubernetes/serviceaccount")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getJwtAuthenticationStrategyArgumentsProvider")
    void create(Class<?> clazz, String issuer) throws Exception {
        // given
        final DefaultClaims claims = new DefaultClaims();
        claims.setIssuer(issuer);
        final String jwt = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.RS512, loadPrivateKey()).compact();
        // when
        final JwtAuthenticationStrategy jwtAuthenticationStrategy = jwtAuthenticationStrategyFactory.create(jwt);
        // then
        assertEquals(clazz, jwtAuthenticationStrategy.getClass());
        verify(beanFactoryMock, times(1))
                .getBean(clazz);
        verifyNoMoreInteractions(beanFactoryMock);
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

}