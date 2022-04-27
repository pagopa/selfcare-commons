package it.pagopa.selfcare.commons.base.security;

import it.pagopa.selfcare.commons.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static it.pagopa.selfcare.commons.base.security.SelfCareAuthority.ADMIN;
import static it.pagopa.selfcare.commons.base.security.SelfCareAuthority.LIMITED;
import static org.junit.jupiter.api.Assertions.*;

class SelfCareGrantedAuthorityTest {

    @Test
    void SelfCareGrantedAuthority_KoNullRole() {
        // given
        String institutionId = null;
        Collection<ProductGrantedAuthority> roleOnProducts = null;
        // when
        Executable executable = () -> {
            new SelfCareGrantedAuthority(institutionId, roleOnProducts);
        };
        // then
        assertThrows(IllegalArgumentException.class, executable);
    }


    @Test
    void SelfCareGrantedAuthority_KoEmptyRole() {
        // given
        String institutionId = "institutionId";
        Collection<ProductGrantedAuthority> roleOnProducts = Collections.emptyList();
        // when
        Executable executable = () -> new SelfCareGrantedAuthority(institutionId, roleOnProducts);
        // then
        assertThrows(IllegalArgumentException.class, executable);
    }


    @Test
    void getAuthority_ADMIN() {
        // given
        String institutionId = "institutionId";
        ProductGrantedAuthority productRole1 =
                TestUtils.mockInstance(new ProductGrantedAuthority(ADMIN, List.of("productRole1"), "productId1"), 1);
        ProductGrantedAuthority productRole2 =
                TestUtils.mockInstance(new ProductGrantedAuthority(LIMITED, List.of("productRole2"), "productId2"), 2);
        Collection<ProductGrantedAuthority> roleOnProducts =
                List.of(productRole1, productRole2);
        // when
        SelfCareGrantedAuthority grantedAuthority = new SelfCareGrantedAuthority(institutionId, roleOnProducts);
        // then
        assertEquals(institutionId, grantedAuthority.getInstitutionId());
        assertEquals(ADMIN.name(), grantedAuthority.getAuthority());
        assertIterableEquals(new HashSet<>(roleOnProducts), new HashSet<>(grantedAuthority.getRoleOnProducts().values()));
    }


    @Test
    void getAuthority_LIMITED() {
        // given
        String institutionId = "institutionId";
        ProductGrantedAuthority productRole1 =
                TestUtils.mockInstance(new ProductGrantedAuthority(LIMITED, List.of("productRole1"), "productId1"), 1);
        ProductGrantedAuthority productRole2 =
                TestUtils.mockInstance(new ProductGrantedAuthority(LIMITED, List.of("productRole2"), "productId2"), 2);
        Collection<ProductGrantedAuthority> roleOnProducts =
                List.of(productRole1, productRole2);
        // when
        SelfCareGrantedAuthority grantedAuthority = new SelfCareGrantedAuthority(institutionId, roleOnProducts);
        // then
        assertEquals(institutionId, grantedAuthority.getInstitutionId());
        assertEquals(LIMITED.name(), grantedAuthority.getAuthority());
        assertIterableEquals(new HashSet<>(roleOnProducts), new HashSet<>(grantedAuthority.getRoleOnProducts().values()));
    }

}