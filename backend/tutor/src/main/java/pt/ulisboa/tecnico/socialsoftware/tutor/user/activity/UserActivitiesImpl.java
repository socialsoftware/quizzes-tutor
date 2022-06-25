package pt.ulisboa.tecnico.socialsoftware.tutor.user.activity;

import java.util.List;

import pt.ulisboa.tecnico.socialsoftware.common.activity.UserActivities;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

public class UserActivitiesImpl implements UserActivities {

    private final UserService userService;

    public UserActivitiesImpl(UserService userService) {
        this.userService = userService;
    }

    public UserDto createUser(String name, Role role, String username, boolean isActive) {
        return userService.createUser(name, role, username, isActive);
    }

    public void rejectUser(Integer userId) {
        userService.deleteUser(userId);
    }

    public void removeCourseExecution(Integer userId, List<CourseExecutionDto> courseExecutionDtoList) {
        userService.removeCourseExecutions(userId, courseExecutionDtoList);
    }

    public void activateUser(Integer userId) {
        userService.activateUser(userId);
    }
}
