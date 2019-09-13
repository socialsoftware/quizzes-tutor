package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.ResourceNotFoundException;

import javax.validation.Valid;
import java.util.List;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
public class UserController {

    private UserRepository userRepository;

    UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public List<User> getUser(@RequestParam("page") int pageIndex, @RequestParam("size") int pageSize){
        return userRepository.findAll(PageRequest.of(pageIndex, pageSize)).getContent();
    }

    @GetMapping("/users/{userId}")
    public User getUser(@PathVariable Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        return userRepository.save(user);
    }

    @PutMapping("/users/{userId}")
    public User updateUser(@PathVariable Integer userId,
                              @Valid @RequestBody User userRequest) {

        return userRepository.findById(userId)
                .map(user -> {
                    user.setYear(userRequest.getYear());
                    return userRepository.save(user);
                }).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
    }


    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
    }
}