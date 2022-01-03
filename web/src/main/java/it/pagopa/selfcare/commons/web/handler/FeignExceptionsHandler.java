package it.pagopa.selfcare.commons.web.handler;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

import static it.pagopa.selfcare.commons.web.handler.RestExceptionsHandler.UNHANDLED_EXCEPTION;

@Slf4j
@RestControllerAdvice
@ConditionalOnClass(FeignException.class)
public class FeignExceptionsHandler {

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeignException(FeignException e) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpStatus httpStatus = Optional.ofNullable(HttpStatus.resolve(e.status()))
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        if (httpStatus.is4xxClientError()) {
            log.warn(UNHANDLED_EXCEPTION, e);

        } else {
            log.error(UNHANDLED_EXCEPTION, e);
        }

        return new ResponseEntity<>(e.contentUTF8(), httpHeaders, httpStatus);
    }

}