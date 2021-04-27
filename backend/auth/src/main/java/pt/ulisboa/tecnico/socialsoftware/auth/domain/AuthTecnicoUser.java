package pt.ulisboa.tecnico.socialsoftware.auth.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("TECNICO")
public class AuthTecnicoUser extends AuthUser {

    @Column(columnDefinition = "TEXT")
    private String enrolledCoursesAcronyms;

    public AuthTecnicoUser() {}

    public AuthTecnicoUser(UserSecurityInfo userSecurityInfo, String username, String email) {
        super(userSecurityInfo, username, email);
    }

    public String getEnrolledCoursesAcronyms() {
        return enrolledCoursesAcronyms;
    }

    public void setEnrolledCoursesAcronyms(String enrolledCoursesAcronyms) {
        this.enrolledCoursesAcronyms = enrolledCoursesAcronyms;
    }

    @Override
    public Type getType() {return Type.TECNICO;}

}
