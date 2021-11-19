package it.pagopa.selfcare.commons.web.handler;

import it.pagopa.selfcare.commons.web.model.ErrorResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.ServletException;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class RestExceptionsHandler.
 */
@Slf4j
@RestControllerAdvice
public class RestExceptionsHandler {

    /**
     * The Constant UNHANDLED_EXCEPTION.
     */
    public static final String UNHANDLED_EXCEPTION = "unhandled exception: ";


    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResource handleThrowable(Throwable e) {
        log.error(UNHANDLED_EXCEPTION, e);
        return new ErrorResource(e.getMessage());
    }


    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResource handleValidationException(ValidationException e) {
        log.warn(e.getMessage());
        return new ErrorResource(e.getMessage());
    }


    @ExceptionHandler({BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResource handleBindException(BindException e) {
        log.warn(e.getMessage());
        return new ErrorResource(e.getMessage());
    }


    @ExceptionHandler({ServletException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResource handleServletException(ServletException e) {
        log.warn(e.getMessage());
        return new ErrorResource(e.getMessage());
    }


    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    ErrorResource handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e) {
        log.warn(e.getMessage());
        return new ErrorResource(e.getMessage());
    }


    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    ErrorResource handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn(e.getMessage());
        return new ErrorResource(e.getMessage());
    }


    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResource handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn(e.getMessage());
        return new ErrorResource(e.getMessage());
    }


    @ExceptionHandler({MaxUploadSizeExceededException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResource handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn(e.getMessage());
        return new ErrorResource(e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResource handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, List<String>> errorMessage = new HashMap<>();
        e.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            errorMessage.computeIfAbsent(fieldName, s -> new ArrayList<>())
                    .add(error.getCode() + " constraint violation");
        });
        log.warn(errorMessage.toString());
        return new ErrorResource(errorMessage.toString());
    }

}