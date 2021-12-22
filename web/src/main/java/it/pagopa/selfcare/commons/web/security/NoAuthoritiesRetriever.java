package it.pagopa.selfcare.commons.web.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class NoAuthoritiesRetriever implements AuthoritiesRetriever {

    @Override
    public Collection<GrantedAuthority> retrieveAuthorities() {
        return null;
    }

}
