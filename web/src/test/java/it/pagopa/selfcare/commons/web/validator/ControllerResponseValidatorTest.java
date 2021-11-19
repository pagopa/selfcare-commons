package it.pagopa.selfcare.commons.web.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import javax.validation.Validation;

class ControllerResponseValidatorTest {

    private ControllerResponseValidator controllerResponseValidator =
            new DummyControllerResponseValidator(Validation.buildDefaultValidatorFactory().getValidator());

    @Test
    void validateResponse_nullResponse() {
        // when
        controllerResponseValidator.validateResponse(null, null);
    }

    @Test
    void validateResponse_invalidResponse() {
        // given
        DummyResource resource = new DummyResource(null);
        // when
        Executable executable = () -> controllerResponseValidator.validateResponse(null, resource);
        // then
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, executable);
        Assertions.assertEquals("{dummy=[NotNull constraint violation]}", exception.getMessage());
    }

    @Test
    void validateResponse_validResponse() {
        // given
        DummyResource resource = new DummyResource("dummy");
        // when
        controllerResponseValidator.validateResponse(null, resource);
    }
}