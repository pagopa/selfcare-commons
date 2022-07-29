package it.pagopa.selfcare.commons.web.model.mapper;

import it.pagopa.selfcare.commons.web.model.Page;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageMapperTest {

    @Test
    void map_nullInput() {
        // given
        final org.springframework.data.domain.Page<Object> page = null;
        // when
        final Page<Object> map = PageMapper.map(page);
        // then
        assertNull(map);
    }

    @Test
    void map_notNullInput() {
        // given
        String content = "content";
        final List<String> contents = List.of(content);
        final Pageable pageable = Pageable.unpaged();
        final org.springframework.data.domain.Page<String> page = PageableExecutionUtils.getPage(contents, pageable, () -> 0);
        // when
        final Page<String> mappedPage = PageMapper.map(page);
        // then
        assertNotNull(mappedPage);
        assertIterableEquals(contents, mappedPage.getContent());
        assertNotNull(mappedPage.getPage());
        assertEquals(page.getNumber(), mappedPage.getPage().getNumber());
        assertEquals(page.getSize(), mappedPage.getPage().getSize());
        assertEquals(page.getTotalElements(), mappedPage.getPage().getTotalElements());
        assertEquals(page.getTotalPages(), mappedPage.getPage().getTotalPages());
    }

}