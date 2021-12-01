package it.pagopa.selfcare.commons.base.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ProductGrantedAuthorityTest {

    @Test
    void ProductGrantedAuthority_nullSelcRole() {
        // given
        Authority selcRole = null;
        String productRole = "";
        String productCode = "";
        // when
        Executable executable = () -> new ProductGrantedAuthority(selcRole, productRole, productCode);
        // then
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, executable);
        Assertions.assertEquals("A Self Care granted authority is required", e.getMessage());
    }

    @Test
    void ProductGrantedAuthority_nullProductRole() {
        // given
        Authority selcRole = Authority.ADMIN;
        String productRole = null;
        String productCode = "";
        // when
        Executable executable = () -> new ProductGrantedAuthority(selcRole, productRole, productCode);
        // then
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, executable);
        Assertions.assertEquals("A Product granted authority textual representation is required", e.getMessage());
    }

    @Test
    void ProductGrantedAuthority_nullProductCode() {
        // given
        Authority selcRole = Authority.ADMIN;
        String productRole = "";
        String productCode = null;
        // when
        Executable executable = () -> new ProductGrantedAuthority(selcRole, productRole, productCode);
        // then
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, executable);
        Assertions.assertEquals("A product code is required", e.getMessage());
    }


    @Test
    void getters() {
        // given
        Authority selcRole = Authority.ADMIN;
        String productRole = "";
        String productCode = "";
        // when
        ProductGrantedAuthority authority = new ProductGrantedAuthority(selcRole, productRole, productCode);
        // then
        Assertions.assertEquals(selcRole.name(), authority.getAuthority());
        Assertions.assertEquals(selcRole, authority.getSelcRole());
        Assertions.assertEquals(productRole, authority.getProductRole());
        Assertions.assertEquals(productCode, authority.getProductCode());
    }

}