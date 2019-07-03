package com.example.tutor.user;

import com.example.tutor.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    private UserRepository userRepository;

    UserController(UserRepository repository) {
        this.userRepository = repository;
    }

    @GetMapping("/users")
    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @GetMapping("/users/{user_id}")
    public User getUser(@PathVariable Integer user_id) {
        return userRepository.findById(user_id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + user_id));
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        return userRepository.save(user);
    }

    @PutMapping("/users/{user_id}")
    public User updateUser(@PathVariable Integer user_id,
                              @Valid @RequestBody User userRequest) {

        return userRepository.findById(user_id)
                .map(user -> {
                    user.setYear(userRequest.getYear());
                    return userRepository.save(user);
                }).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + user_id));
    }


    @DeleteMapping("/users/{user_id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer user_id) {
        return userRepository.findById(user_id)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + user_id));
    }
}