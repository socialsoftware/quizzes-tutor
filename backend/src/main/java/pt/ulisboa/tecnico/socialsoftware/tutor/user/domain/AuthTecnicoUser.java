package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "auth_tecnico_users")
@DiscriminatorValue("TECNICO")
public class AuthTecnicoUser extends AuthUser {

    @Column(columnDefinition = "TEXT")
    private String enrolledCoursesAcronyms;

    public AuthTecnicoUser() {}

    public AuthTecnicoUser(User user, String username, String email) {
        super(user, username, email);
    }

    public String getEnrolledCoursesAcronyms() {
        return enrolledCoursesAcronyms;
    }

    public void setEnrolledCoursesAcronyms(String enrolledCoursesAcronyms) {
        this.enrolledCoursesAcronyms = enrolledCoursesAcronyms;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public Type getType() {return Type.TECNICO;}

}
