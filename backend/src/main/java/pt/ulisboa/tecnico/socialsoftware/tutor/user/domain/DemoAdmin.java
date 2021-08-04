package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(User.UserTypes.DEMO_ADMIN)
public class DemoAdmin extends User {
    public DemoAdmin() {}

    public DemoAdmin(String name, String username, String email, boolean isAdmin, AuthUser.Type type) {
        super(name, username, email, Role.DEMO_ADMIN, isAdmin, type);
    }

    public DemoAdmin(String name, boolean isAdmin){
        super(name, Role.DEMO_ADMIN, isAdmin);
    }
}
