package pt.ulisboa.tecnico.socialsoftware.auth.activity.createUserWithAuth;

import java.util.List;

import com.uber.cadence.activity.ActivityMethod;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.utils.Constants;

public interface CreateUserWithAuthActivities {

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.AUTH_TASK_LIST)
    void rejectAuthUser(Integer authUserId);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.AUTH_TASK_LIST)
    void approveAuthUser(Integer authUserId, Integer userId, List<CourseExecutionDto> courseExecutionList);

}
