package it.pagopa.selfcare.commons.base.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class SelfCareGrantedAuthority implements GrantedAuthority {

    public static final Set<String> PRODUCTS_BASED_AUTHORITIES = Set.of("TECH_REF", "ADMIN_REF");

    private final String role;
    private final Set<String> products;


    public SelfCareGrantedAuthority(String role) {
        this(role, null);
    }


    public SelfCareGrantedAuthority(String role, Collection<String> products) {
        Assert.hasText(role, "A granted authority textual representation is required");
        this.role = role;
        if (PRODUCTS_BASED_AUTHORITIES.contains(role)) {
            if (products == null) {
                this.products = Collections.emptySet();
            } else {
                this.products = Set.copyOf(products);
            }
        } else {
            this.products = null;
        }
    }


    @Override
    public String getAuthority() {
        return this.role;
    }


    public Collection<String> getProducts() {
        return products;
    }

}
