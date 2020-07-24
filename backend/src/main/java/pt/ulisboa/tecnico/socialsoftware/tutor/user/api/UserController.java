package pt.ulisboa.tecnico.socialsoftware.tutor.user.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UsersIdsDto;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping ("/courses/executions/{executionId}/users")
    @PreAuthorize("hasRole('ROLE_DEMO_ADMIN') or hasRole('ROLE_ADMIN')")
    public ExternalUserDto createExternalUser(@PathVariable int executionId, @Valid @RequestBody ExternalUserDto externalUserDto){
        return userService.createExternalUser(executionId,externalUserDto);
    }

    @PostMapping ("/auth/registration/confirm")
    public ExternalUserDto confirmRegistration(@RequestBody ExternalUserDto externalUserDto){
        return userService.confirmRegistration(externalUserDto);
    }

    @PostMapping("/courses/executions/{executionId}/csv")
    @PreAuthorize("hasRole('ROLE_DEMO_ADMIN') or (hasRole('ROLE_ADMIN') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public String uploadCSVFile(@PathVariable Integer executionId, @RequestParam("file") MultipartFile file) throws IOException {
        userService.importListOfUsers(file.getInputStream(), executionId);
        return file.getOriginalFilename();
    }

    @PostMapping("/users/delete/inactive")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DEMO_ADMIN')")
    public void deleteExternalInactiveUsers(@Valid @RequestBody List<Integer> usersIds) {
        userService.deleteExternalInactiveUsers(usersIds);
    }


}
