package pt.ulisboa.tecnico.socialsoftware.auth.activity.createUserWithAuth;

import java.util.List;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;

public interface CreateUserWithAuthActivities {

    void rejectAuthUser(Integer authUserId);

    void approveAuthUser(Integer authUserId, Integer userId, List<CourseExecutionDto> courseExecutionList);

}
