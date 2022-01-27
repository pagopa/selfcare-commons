package it.pagopa.selfcare.commons.base.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.AuthenticatedPrincipal;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
public class SelfCareUser implements AuthenticatedPrincipal {

    private final String id;
    private String email;

    private SelfCareUser(String id) {
        this.id = id;
    }

    public static SelfCareUserBuilder builder(String id) {
        return new SelfCareUserBuilder(id);
    }


    @Override
    public String getName() {
        return id;
    }

    public static class SelfCareUserBuilder {

        private final String id;
        private String email;

        private SelfCareUserBuilder(String id) {
            this.id = id;
        }

        public SelfCareUserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public SelfCareUser build() {
            SelfCareUser user = new SelfCareUser(id);
            user.email = email;
            return user;
        }

    }

}
