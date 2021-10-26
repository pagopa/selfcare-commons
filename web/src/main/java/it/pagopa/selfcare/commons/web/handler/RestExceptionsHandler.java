package it.pagopa.selfcare.commons.web.handler;

import it.pagopa.selfcare.commons.web.model.ErrorResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

/**
 * The Class RestExceptionsHandler.
 */
@ControllerAdvice
@Slf4j
public class RestExceptionsHandler {

    /**
     * The Constant UNHANDLED_EXCEPTION.
     */
    public static final String UNHANDLED_EXCEPTION = "unhandled exception: ";


    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    private ErrorResource handleThrowable(Throwable e, HttpServletRequest request, HttpServletResponse response) {
        log.error(UNHANDLED_EXCEPTION, e);
        return new ErrorResource(e.getMessage());
    }

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    private ErrorResource handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request,
                                                             HttpServletResponse response) {
        log.warn(UNHANDLED_EXCEPTION, e);
        return new ErrorResource(e.getMessage());
    }


    @ExceptionHandler({BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    private ErrorResource handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                HttpServletRequest request, HttpServletResponse response) {
        log.warn(UNHANDLED_EXCEPTION, e);
        return new ErrorResource(e.getMessage());
    }

    @ExceptionHandler({ServletException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    private ErrorResource handleMissingServletRequestParameterException(MissingServletRequestParameterException e,
                                                                        HttpServletRequest request, HttpServletResponse response) {
        log.warn(UNHANDLED_EXCEPTION, e);
        return new ErrorResource(e.getMessage());
    }


    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ResponseBody
    private ErrorResource handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e,
                                                                    HttpServletRequest request, HttpServletResponse response) {
        log.warn(UNHANDLED_EXCEPTION, e);
        return new ErrorResource(e.getMessage());
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    private ErrorResource handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e,
                                                                       HttpServletRequest request, HttpServletResponse response) {
        log.warn(UNHANDLED_EXCEPTION, e);
        return new ErrorResource(e.getMessage());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    private ErrorResource handleInvalidParam(MethodArgumentTypeMismatchException e, HttpServletRequest request,
                                             HttpServletResponse response) {
        log.warn(UNHANDLED_EXCEPTION, e);
        return new ErrorResource(e.getMessage());
    }

}