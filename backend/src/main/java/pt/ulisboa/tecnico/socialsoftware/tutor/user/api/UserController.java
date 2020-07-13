package pt.ulisboa.tecnico.socialsoftware.tutor.user.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto;

import javax.validation.Valid;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping ("/courses/executions/{executionId}/users")
    @PreAuthorize("hasRole('ROLE_DEMO_ADMIN') or hasRole('ROLE_ADMIN')")
    public ExternalUserDto createExternalUser(@PathVariable int executionId, @Valid @RequestBody ExternalUserDto externalUserDto){
        return userService.createExternalUser(executionId,externalUserDto);
    }

    @PostMapping ("/users/registration/confirm")
    public ExternalUserDto confirmRegistration(@Valid @RequestBody ExternalUserDto externalUserDto){
        return userService.confirmRegistration(externalUserDto);
    }
}
