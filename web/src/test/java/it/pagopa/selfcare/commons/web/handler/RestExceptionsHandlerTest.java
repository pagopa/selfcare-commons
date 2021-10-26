package it.pagopa.selfcare.commons.web.handler;

import it.pagopa.selfcare.commons.web.model.ErrorResource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestExceptionsHandlerTest {

    private static final String DETAIL_MESSAGE = "detail message";

    private final RestExceptionsHandler handler;


    public RestExceptionsHandlerTest() {
        this.handler = new RestExceptionsHandler();
    }


    @Test
    void handleThrowable() {
        // given
        Throwable exceptionMock = Mockito.mock(Throwable.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource resource = handler.handleThrowable(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }


    @Test
    void handleConstraintViolationException() {
        // given
        Throwable exceptionMock = Mockito.mock(ConstraintViolationException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource resource = handler.handleThrowable(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }


    @Test
    void handleMethodArgumentNotValidException() {
        // given
        Throwable exceptionMock = Mockito.mock(MethodArgumentNotValidException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource resource = handler.handleThrowable(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }


    @Test
    void handleMissingServletRequestParameterException() {
        // given
        Throwable exceptionMock = Mockito.mock(MissingServletRequestParameterException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource resource = handler.handleThrowable(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }


    @Test
    void handleHttpMediaTypeNotAcceptableException() {
        // given
        Throwable exceptionMock = Mockito.mock(HttpMediaTypeNotAcceptableException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource resource = handler.handleThrowable(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }


    @Test
    void handleHttpRequestMethodNotSupportedException() {
        // given
        Throwable exceptionMock = Mockito.mock(HttpRequestMethodNotSupportedException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource resource = handler.handleThrowable(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }


    @Test
    void handleInvalidParam() {
        // given
        Throwable exceptionMock = Mockito.mock(MethodArgumentTypeMismatchException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource resource = handler.handleThrowable(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }

}