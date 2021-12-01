package it.pagopa.selfcare.commons.base.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Set;

import static it.pagopa.selfcare.commons.base.security.Authority.ADMIN;
import static it.pagopa.selfcare.commons.base.security.Authority.LIMITED;

public class SelfCareGrantedAuthority implements GrantedAuthority {

    private final Authority roleOnInstitution;
    private final Set<ProductRole> roleOnProducts;


    public SelfCareGrantedAuthority(Collection<ProductRole> roleOnProducts) {
        Assert.notEmpty(roleOnProducts, "At least one role on product is required");
        boolean isAdminForInstitution = roleOnProducts.stream()
                .anyMatch(productRole -> ADMIN.equals(productRole.getSelcRole()));
        this.roleOnInstitution = isAdminForInstitution ? ADMIN : LIMITED;
        this.roleOnProducts = Set.copyOf(roleOnProducts);
    }


    @Override
    public String getAuthority() {
        return this.roleOnInstitution.name();
    }


    public Collection<ProductRole> getRoleOnProducts() {
        return roleOnProducts;
    }


    @Setter
    @Getter
    @EqualsAndHashCode(of = "productCode")
    public static class ProductRole {
        private Authority selcRole;
        private String productRole;
        private String productCode;
    }

}
