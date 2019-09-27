package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import javax.validation.Valid;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.USER_NOT_FOUND;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<User> getUser(@RequestParam("page") int pageIndex, @RequestParam("size") int pageSize){
        return userRepository.findAll(PageRequest.of(pageIndex, pageSize)).getContent();
    }

    @GetMapping("/users/{userId}")
    public User getUser(@PathVariable Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody UserDto user) {
        return userService.create(user.getName(), user.getUsername(), User.Role.valueOf(user.getRole()));
    }

    @PutMapping("/users/{userId}")
    public User updateUser(@PathVariable Integer userId,
                              @Valid @RequestBody UserDto user) {

        return userRepository.findById(userId)
                .map(usr -> {
                    usr.setYear(user.getYear());
                    return userRepository.save(usr);
                }).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
    }


    @DeleteMapping("/users/{userId}")
    public ResponseEntity deleteUser(@PathVariable Integer userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
    }
}