package it.pagopa.selfcare.commons.web.security;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Set;

@EqualsAndHashCode(of = "role")
@ToString
public class SelfCareGrantedAuthority implements GrantedAuthority {

    private final String role;
    private final Set<String> products;


    public SelfCareGrantedAuthority(String role) {
        this(role, null);
    }


    public SelfCareGrantedAuthority(String role, Collection<String> products) {
        Assert.hasText(role, "A granted authority textual representation is required");
        this.role = role;
        this.products = products != null
                ? Set.copyOf(products)
                : null;
    }


    @Override
    public String getAuthority() {
        return this.role;
    }


    public Collection<String> getProducts() {
        return products;
    }

}
