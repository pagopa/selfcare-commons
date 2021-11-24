package it.pagopa.selfcare.commons.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final HttpServletResponse RESPONSE_MOCK = mock(HttpServletResponse.class);
    private static final FilterChain FILTER_CHAIN_MOCK = mock(FilterChain.class);
    private static final String MDC_UID = "uid";

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock
    private AuthenticationManager authenticationManagerMock;
    @Mock
    private JwtService jwtServiceMock;


    @Test
    void doFilterInternal_authHeaderEmpty() throws ServletException, IOException {
        // given
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        // when
        jwtAuthenticationFilter.doFilterInternal(requestMock, RESPONSE_MOCK, FILTER_CHAIN_MOCK);
        // then
        Assertions.assertNull(MDC.get(MDC_UID));
        verifyNoInteractions(jwtServiceMock, authenticationManagerMock);
    }


    @Test
    void doFilterInternal_authHeaderNotEmptyButNotBearer() throws ServletException, IOException {
        // given
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getHeader(eq(HttpHeaders.AUTHORIZATION)))
                .thenReturn("token");
        // when
        jwtAuthenticationFilter.doFilterInternal(requestMock, RESPONSE_MOCK, FILTER_CHAIN_MOCK);
        // then
        Assertions.assertNull(MDC.get(MDC_UID));
        verifyNoInteractions(jwtServiceMock, authenticationManagerMock);
    }


    @Test
    void doFilterInternal_invalidJwt() throws ServletException, IOException {
        // given
        String token = "token";
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getHeader(eq(HttpHeaders.AUTHORIZATION)))
                .thenReturn("Bearer " + token);
        when(jwtServiceMock.getClaims(any()))
                .thenReturn(Optional.empty());
        // when
        jwtAuthenticationFilter.doFilterInternal(requestMock, RESPONSE_MOCK, FILTER_CHAIN_MOCK);
        // then
        Assertions.assertNull(MDC.get(MDC_UID));
        verify(jwtServiceMock, times(1)).getClaims(any());
        verifyNoMoreInteractions(jwtServiceMock);
        verifyNoInteractions(authenticationManagerMock);
    }


    @Test
    void doFilterInternal_validJwtButAuthKo() throws ServletException, IOException {
        // given
        String token = "token";
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getHeader(eq(HttpHeaders.AUTHORIZATION)))
                .thenReturn("Bearer " + token);
        when(jwtServiceMock.getClaims(any()))
                .thenReturn(Optional.of(mock(Claims.class)));
        // when
        jwtAuthenticationFilter.doFilterInternal(requestMock, RESPONSE_MOCK, FILTER_CHAIN_MOCK);
        // then
        Assertions.assertNull(MDC.get(MDC_UID));
        verify(jwtServiceMock, times(1)).getClaims(any());
        verify(authenticationManagerMock, times(1)).authenticate(any());
        verifyNoMoreInteractions(jwtServiceMock, authenticationManagerMock);
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }


    @Test
    void doFilterInternal_validJwtAndAuthOk() throws ServletException, IOException {
        // given
        String token = "token";
        String subject = "subject";
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getHeader(eq(HttpHeaders.AUTHORIZATION)))
                .thenReturn("Bearer " + token);
        Claims claims = new DefaultClaims();
        claims.setSubject(subject);
        when(jwtServiceMock.getClaims(any()))
                .thenReturn(Optional.of(claims));
        when(authenticationManagerMock.authenticate(any()))
                .thenAnswer(invocationOnMock -> {
                    Assertions.assertEquals(subject, MDC.get(MDC_UID));
                    return new TestingAuthenticationToken("username", "password");
                });
        // when
        jwtAuthenticationFilter.doFilterInternal(requestMock, RESPONSE_MOCK, FILTER_CHAIN_MOCK);
        // then
        Assertions.assertNull(MDC.get(MDC_UID));
        verify(jwtServiceMock, times(1)).getClaims(any());
        verify(authenticationManagerMock, times(1)).authenticate(any());
        verifyNoMoreInteractions(jwtServiceMock, authenticationManagerMock);
        Assertions.assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

}