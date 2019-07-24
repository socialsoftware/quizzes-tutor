package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.InvalidFenixException;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.NotEnrolledException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.sdk.ApplicationConfiguration;
import org.fenixedu.sdk.FenixEduClientImpl;
import org.fenixedu.sdk.FenixEduUserDetails;
import org.fenixedu.sdk.exception.FenixEduClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private UserRepository userRepository;
    private static String COURSE_ACRONYM = "ASof";

    AuthController(UserRepository repository) {
        this.userRepository = repository;
    }

    @PostMapping("/fenix")
    public ResponseEntity<?> fenixAuth(@RequestBody FenixCode data) {

        // Create the client from properties file
        ApplicationConfiguration config = ApplicationConfiguration.fromPropertyFilename("/fenixedu.properties");
        FenixEduClientImpl client;
        FenixEduUserDetails userDetails;

        try {
            client = new FenixEduClientImpl(config);
        } catch (FenixEduClientException e) {
            throw new InvalidFenixException("Wrong server configuration files");
        }

        // Get user's authorization data (access_token and refresh_token) client.
        try {
            userDetails = client.getUserDetailsFromCode(data.getCode());
        } catch (FenixEduClientException e) {
            throw new InvalidFenixException("Wrong user Fenix code");
        }


        // When requesting user's private data, the authorization object must be passed along.
        JsonObject person = client.getPerson(userDetails.getAuthorization());
        String username = person.get("username").toString().replaceAll("^\"|\"$", "");

        // Find if user is in database
        User user = this.userRepository.findByUsername(username);

        // If user is not in database
        if (user == null){
            // Verify if user is attending the course
            JsonArray courses = client.getPersonCourses(userDetails.getAuthorization()).get("attending").getAsJsonArray();

            boolean isInAS = true;
            // TODO change to false
            for (JsonElement course : courses) {
                isInAS |= course.getAsJsonObject().get("acronym").getAsString().equals(COURSE_ACRONYM);
            }

            if (isInAS) {
                user = new User(person.get("name").toString().replaceAll("^\"|\"$", ""), username, User.Role.STUDENT);
                this.userRepository.save(user);
            } else {
                // Verify if user is teaching the course
                courses = client.getPersonCourses(userDetails.getAuthorization()).get("teaching").getAsJsonArray();

                for (JsonElement course : courses) {
                    isInAS |= course.getAsJsonObject().get("acronym").getAsString().equals(COURSE_ACRONYM);
                }

                if (isInAS) {
                    user = new User(person.get("name").toString().replaceAll("^\"|\"$", ""), username, User.Role.TEACHER);
                    this.userRepository.save(user);
                } else {
                    throw new NotEnrolledException("User " + username + " is not enrolled");
                }
            }


        }

        String token = JwtTokenProvider.generateToken(user);
        return ResponseEntity.ok(new JwtAuthenticationResponse(token, user.getName()));

    }

}