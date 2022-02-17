package it.pagopa.selfcare.commons.web.handler;

import it.pagopa.selfcare.commons.web.model.ErrorResource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.ServletException;
import javax.validation.ValidationException;

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
    void handleValidationException() {
        // given
        ValidationException exceptionMock = Mockito.mock(ValidationException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource resource = handler.handleValidationException(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }


    @Test
    void handleBindException() {
        // given
        BindException exceptionMock = Mockito.mock(BindException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource resource = handler.handleBindException(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }


    @Test
    void handleServletException() {
        // given
        ServletException exceptionMock = Mockito.mock(ServletException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource resource = handler.handleServletException(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }


    @Test
    void handleHttpMediaTypeNotAcceptableException() {
        // given
        HttpMediaTypeNotAcceptableException exceptionMock = Mockito.mock(HttpMediaTypeNotAcceptableException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource resource = handler.handleHttpMediaTypeNotAcceptableException(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }


    @Test
    void handleHttpRequestMethodNotSupportedException() {
        // given
        HttpRequestMethodNotSupportedException exceptionMock = Mockito.mock(HttpRequestMethodNotSupportedException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource resource = handler.handleHttpRequestMethodNotSupportedException(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }


    @Test
    void handleMethodArgumentTypeMismatchException() {
        // given
        MethodArgumentTypeMismatchException exceptionMock = Mockito.mock(MethodArgumentTypeMismatchException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource resource = handler.handleMethodArgumentTypeMismatchException(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }


    @Test
    void handleMaxUploadSizeExceededException() {
        // given
        MaxUploadSizeExceededException exceptionMock = Mockito.mock(MaxUploadSizeExceededException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource resource = handler.handleMaxUploadSizeExceededException(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }

    @Test
    void handleMethodArgumentNotValidException() {
        // given
        MethodArgumentNotValidException exceptionMock = Mockito.mock(MethodArgumentNotValidException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn("{}");
        // when
        ErrorResource resource = handler.handleMethodArgumentNotValidException(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals("{}", resource.getMessage());
    }

    @Test
    void handleAccessDeniedException() {
        // given
        AccessDeniedException exceptionMock = Mockito.mock(AccessDeniedException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource resource = handler.handleAccessDeniedException(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }

    @Test
    void handleHttpMessageNotReadableException() {
        // given
        HttpMessageNotReadableException exceptionMock = Mockito.mock(HttpMessageNotReadableException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource resource = handler.handleHttpMessageNotReadableException(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }

}