package pt.ulisboa.tecnico.socialsoftware.tutor.user.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.dto.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.NotificationResponse;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserServiceApplicational;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto.ExternalUserDto;

import javax.validation.Valid;
import java.io.IOException;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserServiceApplicational userServiceApplicational;

    @PostMapping ("/users/create/{executionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN') and hasPermission(#executionId, 'DEMO.ACCESS'))")
    public ExternalUserDto registerExternalUser(@PathVariable int executionId, @Valid @RequestBody ExternalUserDto externalUserDto){
        return userServiceApplicational.registerExternalUser(executionId,externalUserDto);
    }

    @PostMapping("/courses/executions/{executionId}/csv")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN') and hasPermission(#executionId, 'DEMO.ACCESS'))")
    public NotificationResponse<CourseDto> uploadCSVFile(@PathVariable Integer executionId, @RequestParam("file") MultipartFile file) throws IOException {
        return userService.registerListOfUsersTransactional(file.getInputStream(), executionId);
    }
}
