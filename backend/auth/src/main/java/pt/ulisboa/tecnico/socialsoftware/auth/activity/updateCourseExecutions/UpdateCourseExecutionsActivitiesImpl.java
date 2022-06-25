package pt.ulisboa.tecnico.socialsoftware.auth.activity.updateCourseExecutions;

import java.util.List;

import pt.ulisboa.tecnico.socialsoftware.auth.services.local.AuthUserProvidedService;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;

public class UpdateCourseExecutionsActivitiesImpl implements UpdateCourseExecutionsActivities {

    private final AuthUserProvidedService authUserService;

    public UpdateCourseExecutionsActivitiesImpl(AuthUserProvidedService authUserService) {
        this.authUserService = authUserService;
    }

    @Override
    public void undoUpdateCourseExecutions(Integer authUserId) {
        authUserService.undoUpdateCourseExecutions(authUserId);
    }

    @Override
    public void confirmUpdateCourseExecutions(Integer authUserId, String ids,
            List<CourseExecutionDto> courseExecutionDtoList, String email) {
        authUserService.confirmUpdateCourseExecutions(authUserId, ids, courseExecutionDtoList, email);
    }

}
