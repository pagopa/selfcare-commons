package it.pagopa.selfcare.commons.base.security;

import it.pagopa.selfcare.commons.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static it.pagopa.selfcare.commons.base.security.Authority.ADMIN;
import static it.pagopa.selfcare.commons.base.security.Authority.LIMITED;
import static org.junit.jupiter.api.Assertions.*;

class SelfCareGrantedAuthorityTest {

    @Test
    void SelfCareGrantedAuthority_KoNullRole() {
        // given
        Collection<SelfCareGrantedAuthority.ProductRole> roleOnProducts = null;
        // when
        Executable executable = () -> new SelfCareGrantedAuthority(roleOnProducts);
        // then
        assertThrows(IllegalArgumentException.class, executable);
    }


    @Test
    void SelfCareGrantedAuthority_KoEmptyRole() {
        // given
        Collection<SelfCareGrantedAuthority.ProductRole> roleOnProducts = Collections.emptyList();
        // when
        Executable executable = () -> new SelfCareGrantedAuthority(roleOnProducts);
        // then
        assertThrows(IllegalArgumentException.class, executable);
    }


    @Test
    void getAuthority() {
        // given
        SelfCareGrantedAuthority.ProductRole productRole1 = TestUtils.mockInstance(new SelfCareGrantedAuthority.ProductRole(), 1);
        productRole1.setSelcRole(ADMIN);
        SelfCareGrantedAuthority.ProductRole productRole2 = TestUtils.mockInstance(new SelfCareGrantedAuthority.ProductRole(), 2);
        productRole2.setSelcRole(LIMITED);
        Collection<SelfCareGrantedAuthority.ProductRole> roleOnProducts =
                List.of(productRole1, productRole2);
        // when
        SelfCareGrantedAuthority grantedAuthority = new SelfCareGrantedAuthority(roleOnProducts);
        // then
        assertEquals(ADMIN.name(), grantedAuthority.getAuthority());
        assertIterableEquals(new HashSet<>(roleOnProducts), new HashSet<>(grantedAuthority.getRoleOnProducts()));
    }

}