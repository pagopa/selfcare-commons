package it.pagopa.selfcare.commons.base.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class SelfCareGrantedAuthorityTest {

    @Test
    void SelfCareGrantedAuthority_KoNullRole() {
        // given
        String role = null;
        // when
        Executable executable = () -> new SelfCareGrantedAuthority(role);
        // then
        assertThrows(IllegalArgumentException.class, executable);
    }


    @Test
    void SelfCareGrantedAuthority_KoEmptyRole() {
        // given
        String role = "";
        // when
        Executable executable = () -> new SelfCareGrantedAuthority(role);
        // then
        assertThrows(IllegalArgumentException.class, executable);
    }


    @Test
    void getAuthority() {
        // given
        String role = "role";
        // when
        SelfCareGrantedAuthority grantedAuthority = new SelfCareGrantedAuthority(role);
        // then
        assertEquals(role, grantedAuthority.getAuthority());
    }


    @Test
    void getProducts_notProductBasedAuthority() {
        // given
        String role = "role";
        Collection<String> products = Collections.singletonList("product");
        // when
        SelfCareGrantedAuthority grantedAuthority = new SelfCareGrantedAuthority(role, products);
        // then
        assertNull(grantedAuthority.getProducts());
    }


    @Test
    void getProducts_productBasedAuthorityWithNullProducts() {
        // given
        String role = "TECH_REF";
        // when
        SelfCareGrantedAuthority grantedAuthority = new SelfCareGrantedAuthority(role, null);
        // then
        assertNotNull(grantedAuthority.getProducts());
        assertTrue(grantedAuthority.getProducts().isEmpty());
    }


    @Test
    void getProducts_productBasedAuthorityWithNotNullProducts() {
        // given
        String role = "TECH_REF";
        Collection<String> products = Collections.singletonList("product");
        // when
        SelfCareGrantedAuthority grantedAuthority = new SelfCareGrantedAuthority(role, products);
        // then
        assertNotNull(grantedAuthority.getProducts());
        assertFalse(grantedAuthority.getProducts().isEmpty());
        assertIterableEquals(products, grantedAuthority.getProducts());
    }

}