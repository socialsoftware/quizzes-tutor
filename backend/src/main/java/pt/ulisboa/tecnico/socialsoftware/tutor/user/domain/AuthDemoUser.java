package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("DEMO")
public class AuthDemoUser extends AuthUser {

    public AuthDemoUser() {}

    public AuthDemoUser(User user, String username, String email) {
        super(user, username, email);
    }

    @Override
    public Type getType() {return Type.DEMO;}

    @Override
    public boolean isDemoStudent() {
        return getUsername().startsWith("Demo-Student-");
    }
}
