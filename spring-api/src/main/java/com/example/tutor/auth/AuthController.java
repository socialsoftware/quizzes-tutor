package com.example.tutor.auth;

import com.example.tutor.user.User;
import com.example.tutor.user.UserRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.sdk.ApplicationConfiguration;
import org.fenixedu.sdk.FenixEduClientImpl;
import org.fenixedu.sdk.FenixEduUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserRepository userRepository;

    private JwtTokenProvider tokenProvider;

    private static String COURSE_ACRONYM = "MDJ26";
    //TODO ASof

    AuthController(UserRepository repository, JwtTokenProvider tokenProvider) {
        this.userRepository = repository;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("/fenix")
    public ResponseEntity<?> fenixAuth(@RequestParam("code") String code) {

        // Create the client from properties file
        ApplicationConfiguration config = ApplicationConfiguration.fromPropertyFilename("/fenixedu.properties");
        FenixEduClientImpl client = new FenixEduClientImpl(config);

        // Get user's authorization data (access_token and refresh_token) client.
        FenixEduUserDetails userDetails = client.getUserDetailsFromCode(code);

        // When requesting user's private data, the authorization object must be passed along.
        JsonObject person = client.getPerson(userDetails.getAuthorization());

        // Find if user is in database
        User user = this.userRepository.findByUsername(person.get("username").toString());

        // If user is not in database
        if (user == null){
            // Verify if user is attending the course
            JsonArray courses = client.getPersonCourses(userDetails.getAuthorization()).get("attending").getAsJsonArray();
            Boolean isInAS = false;
            for (JsonElement course : courses) {
                isInAS |= course.getAsJsonObject().get("acronym").getAsString().equals(COURSE_ACRONYM);
            }

            if (isInAS) {
                user = new User(person.get("name").toString(), person.get("username").toString(), "student");
                this.userRepository.save(user);
            } else {
                // Verify if user is teaching the course
                courses = client.getPersonCourses(userDetails.getAuthorization()).get("teaching").getAsJsonArray();

                for (JsonElement course : courses) {
                    isInAS |= course.getAsJsonObject().get("acronym").getAsString().equals(COURSE_ACRONYM);
                }

                if (isInAS) {
                    user = new User(person.get("name").toString(), person.get("username").toString(), "teacher");
                    this.userRepository.save(user);
                } else {
                    // TODO error
                }
            }

        }

        String token = tokenProvider.generateToken(user);
        return ResponseEntity.ok(new JwtAuthenticationResponse(token, user.getName()));

    }

}