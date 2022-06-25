package pt.ulisboa.tecnico.socialsoftware.auth.activity.createUserWithAuth;

import java.util.List;

import pt.ulisboa.tecnico.socialsoftware.auth.services.local.AuthUserProvidedService;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;

public class CreateUserWithAuthActivitiesImpl implements CreateUserWithAuthActivities {

    private final AuthUserProvidedService authUserService;

    public CreateUserWithAuthActivitiesImpl(AuthUserProvidedService authUserService) {
        this.authUserService = authUserService;
    }

    @Override
    public void rejectAuthUser(Integer authUserId) {
        authUserService.rejectAuthUser(authUserId);
    }

    @Override
    public void approveAuthUser(Integer authUserId, Integer userId, List<CourseExecutionDto> courseExecutionList) {
        authUserService.approveAuthUser(authUserId, userId, courseExecutionList);
    }

}
