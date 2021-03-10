package pt.ulisboa.tecnico.socialsoftware.tutor.api;

import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.user.dtos.UserDto;

public interface UserInterface {
    UserDto findUserById(Integer userId);
}
