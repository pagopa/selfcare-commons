package it.pagopa.selfcare.commons.base.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductGrantedAuthorityTest {

    @Test
    void ProductGrantedAuthority_nullSelcRole() {
        // given
        SelfCareAuthority selcRole = null;
        Collection<String> productRoles = List.of("productRole");
        String productId = "productId";
        // when
        Executable executable = () -> new ProductGrantedAuthority(selcRole, productRoles, productId);
        // then
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, executable);
        assertEquals("A Self Care granted authority is required", e.getMessage());
    }

    @Test
    void ProductGrantedAuthority_nullProductRole() {
        // given
        SelfCareAuthority selcRole = SelfCareAuthority.ADMIN;
        Collection<String> productRoles = null;
        String productId = "productId";
        // when
        Executable executable = () -> new ProductGrantedAuthority(selcRole, productRoles, productId);
        // then
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, executable);
        assertEquals("At least one Product granted authority textual representation is required", e.getMessage());
    }

    @Test
    void ProductGrantedAuthority_nullProductCode() {
        // given
        SelfCareAuthority selcRole = SelfCareAuthority.ADMIN;
        Collection<String> productRoles = List.of("productRole");
        String productId = "";
        // when
        Executable executable = () -> new ProductGrantedAuthority(selcRole, productRoles, productId);
        // then
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, executable);
        assertEquals("A Product id is required", e.getMessage());
    }


    @Test
    void ProductGrantedAuthority() {
        // given
        SelfCareAuthority selcRole = SelfCareAuthority.ADMIN;
        Collection<String> productRoles = List.of("productRole");
        String productId = "productId";
        // when
        ProductGrantedAuthority authority = new ProductGrantedAuthority(selcRole, productRoles, productId);
        // then
        assertEquals(selcRole.name(), authority.getAuthority());
        assertIterableEquals(productRoles, authority.getProductRoles());
        assertEquals(productId, authority.getProductId());
        ProductGrantedAuthority authority2 = new ProductGrantedAuthority(SelfCareAuthority.LIMITED, List.of("role2"), productId);
        assertEquals(authority2, authority);
        assertEquals(authority2.hashCode(), authority.hashCode());
        ProductGrantedAuthority authority3 = new ProductGrantedAuthority(SelfCareAuthority.ADMIN, productRoles, "productId2");
        assertNotEquals(authority3, authority);
        assertNotEquals(authority3.hashCode(), authority.hashCode());
    }


    @Test
    void mergeProductGrantedAuthority_differentPartyRole() {
        // given
        Collection<String> productRoles = List.of("productRole");
        String productId = "productId";
        ProductGrantedAuthority authority1 = new ProductGrantedAuthority(SelfCareAuthority.ADMIN, productRoles, productId);
        ProductGrantedAuthority authority2 = new ProductGrantedAuthority(SelfCareAuthority.LIMITED, productRoles, productId);
        // when
        Executable executable = () -> ProductGrantedAuthority.MERGE.apply(authority1, authority2);
        // then
        assertThrows(IllegalStateException.class, executable);
    }


    @Test
    void mergeProductGrantedAuthority() {
        // given
        SelfCareAuthority selcRole = SelfCareAuthority.ADMIN;
        String productRole1 = "productRole1";
        String productRole2 = "productRole2";
        String productId = "productId";
        ProductGrantedAuthority authority1 = new ProductGrantedAuthority(selcRole, productRole1, productId);
        ProductGrantedAuthority authority2 = new ProductGrantedAuthority(selcRole, productRole2, productId);
        // when
        ProductGrantedAuthority merged = ProductGrantedAuthority.MERGE.apply(authority1, authority2);
        // then
        assertEquals(productId, merged.getProductId());
        assertEquals(selcRole.toString(), merged.getAuthority());
        assertIterableEquals(List.of(productRole1, productRole2), merged.getProductRoles());
    }

}