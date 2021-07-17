package pt.ulisboa.tecnico.socialsoftware.common.remote;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserCourseExecutionsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;

public interface UserContract {
    UserDto findUser(Integer userId);
    UserCourseExecutionsDto getUserCourseExecutions(Integer userId);
}
