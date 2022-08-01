package it.pagopa.selfcare.commons.connector.rest.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

class PageJacksonModuleTest {

    private final ObjectMapper objectMapper;

    public PageJacksonModuleTest() {
        this.objectMapper = new ObjectMapper().registerModule(new PageJacksonModule());
    }

    @Test
    void test() throws JsonProcessingException {
        String src = "{\"content\":[\"test\"],\"totalElements\":1,\"number\":0,\"size\":1}";
        Page<String> read = objectMapper.readValue(src, new TypeReference<>() {
        });
        Assertions.assertNotNull(read);
        Assertions.assertNotNull(read.getContent());
        Assertions.assertEquals(1, read.getContent().size());
        Assertions.assertEquals("test", read.getContent().get(0));
        Assertions.assertEquals(0, read.getNumber());
        Assertions.assertEquals(1, read.getTotalElements());
        Assertions.assertEquals(1, read.getTotalPages());
        Assertions.assertEquals(1, read.getSize());
    }

}