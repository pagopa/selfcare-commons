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
        Collection<ProductGrantedAuthority> roleOnProducts = null;
        // when
        Executable executable = () -> new SelfCareGrantedAuthority(roleOnProducts);
        // then
        assertThrows(IllegalArgumentException.class, executable);
    }


    @Test
    void SelfCareGrantedAuthority_KoEmptyRole() {
        // given
        Collection<ProductGrantedAuthority> roleOnProducts = Collections.emptyList();
        // when
        Executable executable = () -> new SelfCareGrantedAuthority(roleOnProducts);
        // then
        assertThrows(IllegalArgumentException.class, executable);
    }


    @Test
    void getAuthority() {
        // given
        ProductGrantedAuthority productRole1 =
                TestUtils.mockInstance(new ProductGrantedAuthority(ADMIN, "", ""), 1);
        ProductGrantedAuthority productRole2 =
                TestUtils.mockInstance(new ProductGrantedAuthority(LIMITED, "", ""), 2);
        Collection<ProductGrantedAuthority> roleOnProducts =
                List.of(productRole1, productRole2);
        // when
        SelfCareGrantedAuthority grantedAuthority = new SelfCareGrantedAuthority(roleOnProducts);
        // then
        assertEquals(ADMIN.name(), grantedAuthority.getAuthority());
        assertIterableEquals(new HashSet<>(roleOnProducts), new HashSet<>(grantedAuthority.getRoleOnProducts()));
    }

}