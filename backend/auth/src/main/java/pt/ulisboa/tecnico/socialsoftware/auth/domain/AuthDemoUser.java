package pt.ulisboa.tecnico.socialsoftware.auth.domain;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.auth.AuthUserType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Locale;

@Entity
@DiscriminatorValue("DEMO")
public class AuthDemoUser extends AuthUser {

    public AuthDemoUser() {}

    public AuthDemoUser(UserSecurityInfo userSecurityInfo, String username, String email) {
        super(userSecurityInfo, username, email);
    }

    @Override
    public AuthUserType getType() {return AuthUserType.DEMO;}

    @Override
    public boolean isDemoStudent() {
        return getUsername().toLowerCase(Locale.ROOT).startsWith("demo-student");
    }
}
