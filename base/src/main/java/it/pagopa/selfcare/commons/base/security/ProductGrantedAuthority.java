package it.pagopa.selfcare.commons.base.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Objects;

@Getter
public class ProductGrantedAuthority implements GrantedAuthority {

    private final SelfCareAuthority selfCareAuthority;
    private final String productRole;
    private final String productId;

    public ProductGrantedAuthority(SelfCareAuthority selfCareAuthority, String productRole, String productId) {
        Assert.notNull(selfCareAuthority, "A Self Care granted authority is required");
        Assert.hasText(productRole, "A Product granted authority textual representation is required");
        Assert.hasText(productId, "A Product id is required");
        this.selfCareAuthority = selfCareAuthority;
        this.productRole = productRole;
        this.productId = productId;
    }

    @Override
    public String getAuthority() {
        return selfCareAuthority.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductGrantedAuthority that = (ProductGrantedAuthority) o;
        return productId.equals(that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

}
