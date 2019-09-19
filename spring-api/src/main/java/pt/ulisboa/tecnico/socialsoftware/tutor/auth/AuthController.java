package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.sdk.ApplicationConfiguration;
import org.fenixedu.sdk.FenixEduClientImpl;
import org.fenixedu.sdk.FenixEduUserDetails;
import org.fenixedu.sdk.exception.FenixEduClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.log.LogService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static String COURSE_ACRONYM = "ASof";
    // Pedro, Professor Rito, Professor Prada, Professor Daniel Gonçalves, José
    private static String[] ADMINS = {"ist181002", "ist12628", "ist32219", "ist13898", "ist148794"};

    @Autowired
    private UserService userService;

    @Value("${base.url}")
    private String baseUrl;

    @Value("${oauth.consumer.key}")
    private String oauthConsumerKey;

    @Value("${oauth.consumer.secret}")
    private String oauthConsumerSecret;

    @Value("${callback.url}")
    private String callbackUrl;

    @Autowired
    private LogService logService;

    @PostMapping("/fenix")
    public ResponseEntity fenixAuth(@RequestBody FenixAuthenticationDto data) {

        ApplicationConfiguration config = new ApplicationConfiguration(baseUrl, oauthConsumerKey, oauthConsumerSecret, callbackUrl);
        FenixEduClientImpl client;
        FenixEduUserDetails userDetails;

        try {
            client = new FenixEduClientImpl(config);
        } catch (FenixEduClientException e) {
            throw new TutorException(FENIX_CONFIGURATION_ERROR);
        }

        // Get user's authorization data (access_token and refresh_token) client.
        try {
            userDetails = client.getUserDetailsFromCode(data.getCode());
        } catch (FenixEduClientException e) {
            throw new TutorException(FENIX_ERROR);
        } catch (Exception e) {
            throw new TutorException(FENIX_ERROR);
        }


        // When requesting user's private data, the authorization object must be passed along.
        JsonObject person = client.getPerson(userDetails.getAuthorization());
        String username = person.get("username").toString().replaceAll("^\"|\"$", "");

        // Find if user is in database
        User user = this.userService.findByUsername(username);

        // If user is not in database
        if (user == null){
            // Verify if user is attending the course
            JsonArray courses = client.getPersonCourses(userDetails.getAuthorization()).get("attending").getAsJsonArray();

            boolean isStudent = false;
            for (JsonElement course : courses) {
                isStudent |= course.getAsJsonObject().get("acronym").getAsString().equals(COURSE_ACRONYM);
            }

            if (Arrays.asList(ADMINS).contains(username)){
                user = this.userService.create(person.get("name").toString().replaceAll("^\"|\"$", ""), username, User.Role.ADMIN);
            } else if (isStudent) {
                user = this.userService.create(person.get("name").toString().replaceAll("^\"|\"$", ""), username, User.Role.STUDENT);
            } else {
                // Verify if user is teaching the course
                courses = client.getPersonCourses(userDetails.getAuthorization()).get("teaching").getAsJsonArray();

                boolean isTeacher = false;
                for (JsonElement course : courses) {
                    isTeacher |= course.getAsJsonObject().get("acronym").getAsString().equals(COURSE_ACRONYM);
                }

                if (isTeacher) {
                    user = this.userService.create(person.get("name").toString().replaceAll("^\"|\"$", ""), username, User.Role.TEACHER);
                } else {
                    throw new TutorException(USER_NOT_ENROLLED);
                }
            }
        }

        logService.create(user, LocalDateTime.now(), "LOGIN");

        String token = JwtTokenProvider.generateToken(user);
        return ResponseEntity.ok(new JwtAuthenticationDto(token, user.getRole()));

    }

}