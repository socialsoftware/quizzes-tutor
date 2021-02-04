package pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Locale;

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
        return getUsername().toLowerCase(Locale.ROOT).startsWith("demo-student");
    }
}
