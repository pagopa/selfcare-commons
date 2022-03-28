package it.pagopa.selfcare.commons.web.handler;

import feign.FeignException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FeignExceptionsHandlerTest {

    private final FeignExceptionsHandler handler;

    FeignExceptionsHandlerTest() {
        this.handler = new FeignExceptionsHandler();
    }


    @ParameterizedTest
    @EnumSource(value = HttpStatus.class, names = {"BAD_REQUEST", "INTERNAL_SERVER_ERROR", "OK"})
    void handleFeignException(HttpStatus httpStatus) {
        // given
        String content = "content";
        FeignException exceptionMock = Mockito.mock(FeignException.class);
        Mockito.when(exceptionMock.contentUTF8())
                .thenReturn(content);
        Mockito.when(exceptionMock.status())
                .thenReturn(httpStatus.value());
        // when
        ResponseEntity<String> responseEntity = handler.handleFeignException(exceptionMock);
        // then
        assertNotNull(responseEntity);
        assertEquals(content, responseEntity.getBody());
        assertEquals(httpStatus.is2xxSuccessful() ? HttpStatus.INTERNAL_SERVER_ERROR : httpStatus, responseEntity.getStatusCode());
    }
}