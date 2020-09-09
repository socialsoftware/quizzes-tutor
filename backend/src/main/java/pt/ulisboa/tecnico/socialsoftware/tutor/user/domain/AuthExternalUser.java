package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain;

import org.springframework.security.crypto.password.PasswordEncoder;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("EXTERNAL")
public class AuthExternalUser extends AuthUser {

    @Column(columnDefinition = "boolean default false")
    private Boolean active;

    public AuthExternalUser() {}

    public AuthExternalUser(User user, String username, String email) {
        super(user, username, email);
        setActive(false);
        checkRole(isActive());
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void confirmRegistration(PasswordEncoder passwordEncoder, String confirmationToken, String password) {
        checkConfirmationToken(confirmationToken);
        setPassword(passwordEncoder.encode(password));
        setActive(true);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public Type getType() {return Type.EXTERNAL;}
}
