package pt.ulisboa.tecnico.socialsoftware.auth.activity.updateCourseExecutions;

import java.util.List;

import com.uber.cadence.activity.ActivityMethod;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.utils.Constants;

public interface UpdateCourseExecutionsActivities {

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.AUTH_TASK_LIST)
    void undoUpdateCourseExecutions(Integer authUserId);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.AUTH_TASK_LIST)
    void confirmUpdateCourseExecutions(Integer authUserId, String ids,
            List<CourseExecutionDto> courseExecutionDtoList, String email);
}
