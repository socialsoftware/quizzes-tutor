package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "auth_tecnico_users")
@DiscriminatorValue("EXTERNAL")
public class AuthExternalUser extends AuthUser {


    public AuthExternalUser() {}

    public AuthExternalUser(User user, String username, String email) {
        super(user, username, email);
        setActive(false);
    }

}
