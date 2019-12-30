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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.log.LogService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.AuthUserDto;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.*;

@Service
public class AuthService {
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

    @Autowired
    private CourseRepository courseRepository;

    @Retryable(
      value = { SQLException.class },
      maxAttempts = 3,
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AuthenticationResponseDto fenixAuth(@RequestBody FenixAuthenticationDto data) {

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
        String username = String.valueOf(person.get("username")).replaceAll("^\"|\"$", "");
        ArrayList<Course> courses = new ArrayList<>();

        // Find if user is in database
        User user = this.userService.findByUsername(username);

        // If user is in database
        if (user != null){
            logService.create(user, LocalDateTime.now(), "LOGIN");

            return new AuthenticationResponseDto(JwtTokenProvider.generateToken(user), new AuthUserDto(user));
        }
            // Verify if user is attending the course
            JsonObject coursesJson = client.getPersonCourses(userDetails.getAuthorization());
            JsonArray attendingCoursesJson = coursesJson.get("attending").getAsJsonArray();

            boolean isStudent = false;
            for (JsonElement courseJson : attendingCoursesJson) {
                Course course = courseRepository.findByName(courseJson.getAsJsonObject().get("name").getAsString());
                if (course != null) {
                    isStudent = true;
                    courses.add(course);
                }
            }

            if (isStudent) {
                user = this.userService.createUser(String.valueOf(person.get("name")).replaceAll("^\"|\"$", ""), username, User.Role.STUDENT);
                logService.create(user, LocalDateTime.now(), "LOGIN");

                String token = JwtTokenProvider.generateToken(user);
                return new AuthenticationResponseDto(token, user.getRole().name());





            JsonArray teachingCoursesJson = coursesJson.get("teaching").getAsJsonArray();



            if (isStudent) {
                user = this.userService.createUser(String.valueOf(person.get("name")).replaceAll("^\"|\"$", ""), username, User.Role.STUDENT);
            } else {
                // Verify if user is teaching the course
                coursesJson = client.getPersonCourses(userDetails.getAuthorization()).get("teaching").getAsJsonArray();

                boolean isTeacher = false;
                for (JsonElement course : coursesJson) {
                    isTeacher |= course.getAsJsonObject().get("acronym").getAsString().equals(COURSE_ACRONYM);
                }

                if (isTeacher) {
                    user = this.userService.createUser(String.valueOf(person.get("name")).replaceAll("^\"|\"$", ""), username, User.Role.TEACHER);
                } else {
                    throw new TutorException(USER_NOT_ENROLLED);
                }
            }
        }

        logService.create(user, LocalDateTime.now(), "LOGIN");

        String token = JwtTokenProvider.generateToken(user);
        return new AuthenticationResponseDto(token, user.getRole().name());
    }
}
