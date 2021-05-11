package pt.ulisboa.tecnico.socialsoftware.common.remote;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;

import java.util.List;

public interface UserContract {
    UserDto findUser(Integer userId);
    List<CourseExecutionDto> getCourseExecutions(Integer userId);
}
