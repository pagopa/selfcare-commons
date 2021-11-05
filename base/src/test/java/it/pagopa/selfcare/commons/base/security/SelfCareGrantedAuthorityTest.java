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
    void getProducts_null() {
        // given
        String role = "role";
        // when
        SelfCareGrantedAuthority grantedAuthority = new SelfCareGrantedAuthority(role);
        // then
        assertNull(grantedAuthority.getProducts());
    }


    @Test
    void getProducts_empty() {
        // given
        String role = "role";
        Collection<String> products = Collections.emptyList();
        // when
        SelfCareGrantedAuthority grantedAuthority = new SelfCareGrantedAuthority(role, products);
        // then
        assertTrue(grantedAuthority.getProducts().isEmpty());
    }


    @Test
    void getProducts_notEmpty() {
        // given
        String role = "role";
        Collection<String> products = Collections.singletonList("product");
        // when
        SelfCareGrantedAuthority grantedAuthority = new SelfCareGrantedAuthority(role, products);
        // then
        assertIterableEquals(products, grantedAuthority.getProducts());
    }

}