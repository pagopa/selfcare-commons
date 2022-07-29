package it.pagopa.selfcare.commons.web.model.mapper;

import it.pagopa.selfcare.commons.web.model.Page;

public class PageMapper {

    public static <T> Page<T> map(org.springframework.data.domain.Page<T> page) {
        Page<T> result = null;
        if (page != null) {
            result = new Page<>();
            result.setContent(page.getContent());
            final Page.PageInfo pageInfo = new Page.PageInfo();
            pageInfo.setTotalElements(page.getTotalElements());
            pageInfo.setTotalPages(page.getTotalPages());
            pageInfo.setNumber(page.getNumber());
            pageInfo.setSize(page.getSize());
            result.setPage(pageInfo);
        }
        return result;
    }

}
