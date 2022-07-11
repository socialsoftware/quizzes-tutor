package pt.ulisboa.tecnico.socialsoftware.common.activity;

import java.util.List;

import com.uber.cadence.activity.ActivityMethod;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.utils.CadenceConstants;

public interface CourseExecutionActivities {

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = CadenceConstants.TUTOR_TASK_LIST)
    CourseExecutionDto getCourseExecution(Integer courseExecutionId);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = CadenceConstants.TUTOR_TASK_LIST)
    void removeCourseExecution(Integer userId, Integer courseExecutionId);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = CadenceConstants.TUTOR_TASK_LIST)
    void addCourseExecutions(Integer userId, List<CourseExecutionDto> courseExecutionDtoList);

}
