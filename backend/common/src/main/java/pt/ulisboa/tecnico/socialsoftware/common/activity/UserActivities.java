package pt.ulisboa.tecnico.socialsoftware.common.activity;

import java.util.List;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;

public interface UserActivities {

    void activateUser(Integer userId);

    // void addCourseExecution();

    UserDto createUser(String name, Role role, String username, boolean isActive);

    void rejectUser(Integer userId);

    void removeCourseExecution(Integer userId, List<CourseExecutionDto> courseExecutionDtoList);

}
