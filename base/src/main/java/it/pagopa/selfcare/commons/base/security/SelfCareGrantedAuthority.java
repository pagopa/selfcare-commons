package it.pagopa.selfcare.commons.base.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.commons.base.security.SelfCareAuthority.ADMIN;
import static it.pagopa.selfcare.commons.base.security.SelfCareAuthority.LIMITED;

public class SelfCareGrantedAuthority implements GrantedAuthority {

    private final String institutionId;
    private final SelfCareAuthority roleOnInstitution;
    private final Map<String, ProductGrantedAuthority> roleOnProducts;


    public SelfCareGrantedAuthority(String institutionId, Collection<ProductGrantedAuthority> roleOnProducts) {
        Assert.hasText(institutionId, "An Institution id is required");
        Assert.notEmpty(roleOnProducts, "At least one Product Granted SelfCareAuthority is required");
        this.institutionId = institutionId;
        boolean isAdminForInstitution = roleOnProducts.stream()
                .anyMatch(productRole -> ADMIN.name().equals(productRole.getAuthority()));
        this.roleOnInstitution = isAdminForInstitution ? ADMIN : LIMITED;
        this.roleOnProducts = roleOnProducts.stream()
                .collect(Collectors.toUnmodifiableMap(ProductGrantedAuthority::getProductId, Function.identity()));
    }


    @Override
    public String getAuthority() {
        return this.roleOnInstitution.name();
    }


    public String getInstitutionId() {
        return institutionId;
    }


    public Map<String, ProductGrantedAuthority> getRoleOnProducts() {
        return roleOnProducts;
    }

}
