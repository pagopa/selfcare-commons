package it.pagopa.selfcare.commons.base.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

@Getter
@EqualsAndHashCode(of = "productCode")
public class ProductGrantedAuthority implements GrantedAuthority {

    private final Authority selcRole;
    private final String productRole;
    private final String productCode;

    public ProductGrantedAuthority(Authority selcRole, String productRole, String productCode) {
        Assert.notNull(selcRole, "A Self Care granted authority is required");
        Assert.notNull(productRole, "A Product granted authority textual representation is required");
        Assert.notNull(productCode, "A product code is required");
        this.selcRole = selcRole;
        this.productRole = productRole;
        this.productCode = productCode;
    }

    @Override
    public String getAuthority() {
        return selcRole.name();
    }

}
