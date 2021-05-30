package pt.ulisboa.tecnico.socialsoftware.auth.domain;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.auth.AuthUserType;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.UnsupportedStateTransitionException;
import pt.ulisboa.tecnico.socialsoftware.common.utils.DateHandler;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUserState.APPROVED;
import static pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUserState.UPDATE_PENDING;

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

    public void authUserBeginUpdateCourseExecutions() {
        switch (getState()) {
            case READY_FOR_UPDATE:
                setState(UPDATE_PENDING);
                break;
            default:
                throw new UnsupportedStateTransitionException(getState());
        }
    }

    public void authUserUndoUpdateCourseExecutions() {
        switch (getState()) {
            case UPDATE_PENDING:
                setState(APPROVED);
                break;
            default:
                throw new UnsupportedStateTransitionException(getState());
        }
    }

    public void authUserconfirmUpdateCourseExecutions(String ids, List<CourseExecutionDto> courseExecutionDtoList) {
        switch (getState()) {
            case UPDATE_PENDING:
                for(CourseExecutionDto dto : courseExecutionDtoList) {
                    addCourseExecution(dto.getCourseExecutionId());
                }

                if (ids != null) {
                    // Used for Tecnico teachers
                    setEnrolledCoursesAcronyms(ids);
                }
                setLastAccess(DateHandler.now());
                setState(APPROVED);
                break;
            default:
                throw new UnsupportedStateTransitionException(getState());
        }
    }
}
