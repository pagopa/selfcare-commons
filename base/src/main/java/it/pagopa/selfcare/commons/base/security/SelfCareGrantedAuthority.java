package it.pagopa.selfcare.commons.base.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Set;

import static it.pagopa.selfcare.commons.base.security.Authority.ADMIN;
import static it.pagopa.selfcare.commons.base.security.Authority.LIMITED;

public class SelfCareGrantedAuthority implements GrantedAuthority {

    private final Authority roleOnInstitution;
    private final Set<ProductGrantedAuthority> roleOnProducts;


    public SelfCareGrantedAuthority(Collection<ProductGrantedAuthority> roleOnProducts) {
        Assert.notEmpty(roleOnProducts, "At least one Product Granted Authority is required");
        boolean isAdminForInstitution = roleOnProducts.stream()
                .anyMatch(productRole -> ADMIN.name().equals(productRole.getAuthority()));
        this.roleOnInstitution = isAdminForInstitution ? ADMIN : LIMITED;
        this.roleOnProducts = Set.copyOf(roleOnProducts);
    }


    @Override
    public String getAuthority() {
        return this.roleOnInstitution.name();
    }


    public Collection<ProductGrantedAuthority> getRoleOnProducts() {
        return roleOnProducts;
    }


}
