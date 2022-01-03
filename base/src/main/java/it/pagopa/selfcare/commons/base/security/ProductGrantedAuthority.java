package it.pagopa.selfcare.commons.base.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

@ToString
@EqualsAndHashCode(of = "productId")
public class ProductGrantedAuthority implements GrantedAuthority {

    private final SelfCareAuthority selfCareAuthority;
    @Getter
    private final String productRole;
    @Getter
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

}
