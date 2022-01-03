package it.pagopa.selfcare.commons.base.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ProductGrantedAuthorityTest {

    @Test
    void ProductGrantedAuthority_nullSelcRole() {
        // given
        SelfCareAuthority selcRole = null;
        String productRole = "productRole";
        String productId = "productId";
        // when
        Executable executable = () -> new ProductGrantedAuthority(selcRole, productRole, productId);
        // then
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, executable);
        Assertions.assertEquals("A Self Care granted authority is required", e.getMessage());
    }

    @Test
    void ProductGrantedAuthority_nullProductRole() {
        // given
        SelfCareAuthority selcRole = SelfCareAuthority.ADMIN;
        String productRole = null;
        String productId = "productId";
        // when
        Executable executable = () -> new ProductGrantedAuthority(selcRole, productRole, productId);
        // then
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, executable);
        Assertions.assertEquals("A Product granted authority textual representation is required", e.getMessage());
    }

    @Test
    void ProductGrantedAuthority_nullProductCode() {
        // given
        SelfCareAuthority selcRole = SelfCareAuthority.ADMIN;
        String productRole = "productRole";
        String productId = "";
        // when
        Executable executable = () -> new ProductGrantedAuthority(selcRole, productRole, productId);
        // then
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, executable);
        Assertions.assertEquals("A Product id is required", e.getMessage());
    }


    @Test
    void ProductGrantedAuthority() {
        // given
        SelfCareAuthority selcRole = SelfCareAuthority.ADMIN;
        String productRole = "productRole";
        String productId = "productId";
        // when
        ProductGrantedAuthority authority = new ProductGrantedAuthority(selcRole, productRole, productId);
        // then
        Assertions.assertEquals(selcRole.name(), authority.getAuthority());
        Assertions.assertEquals(productRole, authority.getProductRole());
        Assertions.assertEquals(productId, authority.getProductId());
        ProductGrantedAuthority authority2 = new ProductGrantedAuthority(SelfCareAuthority.LIMITED, "role2", productId);
        Assertions.assertEquals(authority2, authority);
        Assertions.assertEquals(authority2.hashCode(), authority.hashCode());
        ProductGrantedAuthority authority3 = new ProductGrantedAuthority(SelfCareAuthority.ADMIN, productRole, "productId2");
        Assertions.assertNotEquals(authority3, authority);
        Assertions.assertNotEquals(authority3.hashCode(), authority.hashCode());
    }

}