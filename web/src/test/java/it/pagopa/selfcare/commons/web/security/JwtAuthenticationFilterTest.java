package it.pagopa.selfcare.commons.web.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final HttpServletResponse RESPONSE_MOCK = mock(HttpServletResponse.class);
    private static final FilterChain FILTER_CHAIN_MOCK = mock(FilterChain.class);

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private AuthenticationManager authenticationManagerMock;

    @Captor
    private ArgumentCaptor<JwtAuthenticationToken> jwtAuthenticationTokenCaptor;


    @Test
    void doFilterInternal_authHeaderEmpty() throws ServletException, IOException {
        // given
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        // when
        jwtAuthenticationFilter.doFilterInternal(requestMock, RESPONSE_MOCK, FILTER_CHAIN_MOCK);
        // then
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationManagerMock, times(1))
                .authenticate(jwtAuthenticationTokenCaptor.capture());
        Assertions.assertNotNull(jwtAuthenticationTokenCaptor.getValue());
        Assertions.assertNull(jwtAuthenticationTokenCaptor.getValue().getCredentials());
        verifyNoMoreInteractions(authenticationManagerMock);
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
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationManagerMock, times(1))
                .authenticate(jwtAuthenticationTokenCaptor.capture());
        Assertions.assertNotNull(jwtAuthenticationTokenCaptor.getValue());
        Assertions.assertNull(jwtAuthenticationTokenCaptor.getValue().getCredentials());
        verifyNoMoreInteractions(authenticationManagerMock);
    }


    @Test
    void doFilterInternal_invalidToken() throws ServletException, IOException {
        // given
        String token = "token";
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getHeader(eq(HttpHeaders.AUTHORIZATION)))
                .thenReturn("Bearer " + token);
        // when
        jwtAuthenticationFilter.doFilterInternal(requestMock, RESPONSE_MOCK, FILTER_CHAIN_MOCK);
        // then
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationManagerMock, times(1))
                .authenticate(jwtAuthenticationTokenCaptor.capture());
        Assertions.assertNotNull(jwtAuthenticationTokenCaptor.getValue());
        Assertions.assertEquals(token, jwtAuthenticationTokenCaptor.getValue().getCredentials());
        verifyNoMoreInteractions(authenticationManagerMock);
    }


    @Test
    void doFilterInternal_withBearerTokenButAuthKo() throws ServletException, IOException {
        // given
        String token = "token";
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getHeader(eq(HttpHeaders.AUTHORIZATION)))
                .thenReturn("Bearer " + token);
        doThrow(RuntimeException.class)
                .when(authenticationManagerMock)
                .authenticate(any());
        String mdcKey = "key";
        MDC.put(mdcKey, "val");
        // when
        jwtAuthenticationFilter.doFilterInternal(requestMock, RESPONSE_MOCK, FILTER_CHAIN_MOCK);
        // then
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationManagerMock, times(1))
                .authenticate(jwtAuthenticationTokenCaptor.capture());
        Assertions.assertNotNull(jwtAuthenticationTokenCaptor.getValue());
        Assertions.assertEquals(token, jwtAuthenticationTokenCaptor.getValue().getCredentials());
        Assertions.assertNull(MDC.get(mdcKey));
        verifyNoMoreInteractions(authenticationManagerMock);
    }


    @Test
    void doFilterInternal_validJwtAndAuthOk() throws ServletException, IOException {
        // given
        String token = "token";
        String mdcKey = "key";
        String mdcVal = "val";
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getHeader(eq(HttpHeaders.AUTHORIZATION)))
                .thenReturn("Bearer " + token);
        when(authenticationManagerMock.authenticate(any()))
                .thenAnswer(invocationOnMock -> {
                    MDC.put(mdcKey, mdcVal);
                    return new TestingAuthenticationToken("username", "password");
                });
        // when
        jwtAuthenticationFilter.doFilterInternal(requestMock, RESPONSE_MOCK, FILTER_CHAIN_MOCK);
        // then
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationManagerMock, times(1))
                .authenticate(jwtAuthenticationTokenCaptor.capture());
        Assertions.assertNotNull(jwtAuthenticationTokenCaptor.getValue());
        Assertions.assertEquals(token, jwtAuthenticationTokenCaptor.getValue().getCredentials());
        Assertions.assertNull(MDC.get(mdcKey));
        verifyNoMoreInteractions(authenticationManagerMock);
    }

}