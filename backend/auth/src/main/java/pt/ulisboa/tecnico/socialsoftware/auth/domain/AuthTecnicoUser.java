package pt.ulisboa.tecnico.socialsoftware.auth.domain;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.auth.AuthUserType;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.UnsupportedStateTransitionException;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUserState.APPROVED;

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
    public AuthUserType getType() {return AuthUserType.TECNICO;}

    public void authUserUndoUpdateCourseExecutions() {
        switch (getState()) {
            case UPDATE_PENDING:
                setState(APPROVED);
                break;
            default:
                throw new UnsupportedStateTransitionException(getState());
        }
    }

    public void authUserConfirmUpdateCourseExecutions(String ids, List<CourseExecutionDto> courseExecutionDtoList, String email) {
        switch (getState()) {
            case UPDATE_PENDING:
                if (ids != null) {
                    // Used for Tecnico teachers
                    setEnrolledCoursesAcronyms(ids);
                }

                if (email != null) {
                    setEmail(email);
                }

                super.authUserConfirmUpdateCourseExecutions(courseExecutionDtoList);
                break;
            default:
                throw new UnsupportedStateTransitionException(getState());
        }
    }
}
