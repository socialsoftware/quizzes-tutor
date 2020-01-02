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
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.AuthUserDto;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

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
    private CourseExecutionRepository courseExecutionRepository;

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

        JsonObject coursesJson = client.getPersonCourses(userDetails.getAuthorization());

        JsonArray attendingCoursesJson = coursesJson.get("attending").getAsJsonArray();
        Set<CourseExecution> attendingCourses = getCourseExecutions(attendingCoursesJson);

        JsonArray teachingCoursesJson = coursesJson.get("teaching").getAsJsonArray();
        Set<CourseExecution> teachingCourses = getCourseExecutions(teachingCoursesJson);

        User user = this.userService.findByUsername(username);

        // If user is student not in db
        if (user == null && !attendingCourses.isEmpty()) {
            user = this.userService.createUser(String.valueOf(person.get("name")).replaceAll("^\"|\"$", ""), username, User.Role.STUDENT);
        }

        // If user is teacher not in db
        if (user == null && !teachingCourses.isEmpty()) {
            user = this.userService.createUser(String.valueOf(person.get("name")).replaceAll("^\"|\"$", ""), username, User.Role.TEACHER);
        }

        // Update student courses
        if (!attendingCourses.isEmpty()) {
            User student = user;
            attendingCourses.stream().filter(courseExecution -> !student.getCourseExecutions().contains(courseExecution)).forEach(user::addCourse);
            return new AuthenticationResponseDto(JwtTokenProvider.generateToken(user), new AuthUserDto(user));
        }

        // Update teacher courses
        if (!teachingCourses.isEmpty()) {
            User teacher = user;
            teachingCourses.stream().filter(courseExecution -> !teacher.getCourseExecutions().contains(courseExecution)).forEach(user::addCourse);
            return new AuthenticationResponseDto(JwtTokenProvider.generateToken(user), new AuthUserDto(user));
        }

        throw new TutorException(USER_NOT_ENROLLED);
    }

    private Set<CourseExecution> getCourseExecutions(JsonArray attendingCoursesJson) {
        Set<CourseExecution> courses = new HashSet<>();
        for (JsonElement courseJson : attendingCoursesJson) {
            CourseExecution course = courseExecutionRepository.findByAcronym(courseJson.getAsJsonObject().get("acronym").getAsString());
            if (course != null) {
                courses.add(course);
            }
        }
        return courses;
    }
}