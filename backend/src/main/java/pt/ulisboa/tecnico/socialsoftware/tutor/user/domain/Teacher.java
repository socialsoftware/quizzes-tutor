package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;

@Entity
@DiscriminatorValue(User.UserTypes.TEACHER)
public class Teacher extends User {
    public Teacher() {
    }

    public Teacher(String name, String username, String email, boolean isAdmin, AuthUser.Type type) {
        super(name, username, email, Role.TEACHER, isAdmin, type);
    }

    public Teacher(String name, boolean isAdmin) {
        super(name, Role.TEACHER, isAdmin);
    }
}
