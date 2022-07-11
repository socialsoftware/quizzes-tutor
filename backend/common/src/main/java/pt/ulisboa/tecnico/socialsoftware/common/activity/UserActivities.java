package pt.ulisboa.tecnico.socialsoftware.common.activity;

import java.util.List;

import com.uber.cadence.activity.ActivityMethod;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.common.utils.CadenceConstants;

public interface UserActivities {

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = CadenceConstants.TUTOR_TASK_LIST)
    void activateUser(Integer userId);

    // void addCourseExecution();

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = CadenceConstants.TUTOR_TASK_LIST)
    UserDto createUser(String name, Role role, String username, boolean isActive);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = CadenceConstants.TUTOR_TASK_LIST)
    void rejectUser(Integer userId);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = CadenceConstants.TUTOR_TASK_LIST)
    void removeCourseExecution(Integer userId, List<CourseExecutionDto> courseExecutionDtoList);

}
