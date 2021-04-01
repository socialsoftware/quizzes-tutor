package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.NotificationResponse;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto;

import javax.validation.Valid;
import java.io.IOException;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserApplicationalService userApplicationalService;

    @PostMapping ("/users/register/{executionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN') and hasPermission(#executionId, 'DEMO.ACCESS'))")
    public ExternalUserDto registerExternalUser(@PathVariable int executionId, @Valid @RequestBody ExternalUserDto externalUserDto){
        return userApplicationalService.registerExternalUser(executionId, externalUserDto);
    }

    @PostMapping("/users/register/{executionId}/csv")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN') and hasPermission(#executionId, 'DEMO.ACCESS'))")
    public NotificationResponse<CourseExecutionDto> registerExternalUsersCsvFile(@PathVariable Integer executionId, @RequestParam("file") MultipartFile file) throws IOException {
        return userApplicationalService.registerListOfUsers(file.getInputStream(), executionId);
    }

    @PostMapping("/users/register/confirm")
    public ExternalUserDto confirmRegistration(@RequestBody ExternalUserDto externalUserDto){
        return  userApplicationalService.confirmRegistration(externalUserDto);
    }

}
