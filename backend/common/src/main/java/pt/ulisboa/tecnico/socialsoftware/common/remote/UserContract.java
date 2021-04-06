package pt.ulisboa.tecnico.socialsoftware.tutor.api;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;

public interface UserContract {
    UserDto findUserById(Integer userId);
}
