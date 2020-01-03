package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.sdk.ApplicationConfiguration;
import org.fenixedu.sdk.FenixEduClientImpl;
import org.fenixedu.sdk.FenixEduUserDetails;
import org.fenixedu.sdk.exception.FenixEduClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.FENIX_CONFIGURATION_ERROR;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.FENIX_ERROR;

@Component
public class FenixEduInterface {
    @Value("${base.url}")
    private String baseUrl;

    @Value("${oauth.consumer.key}")
    private String oauthConsumerKey;

    @Value("${oauth.consumer.secret}")
    private String oauthConsumerSecret;

    @Value("${callback.url}")
    private String callbackUrl;

    private FenixEduClientImpl client;
    private FenixEduUserDetails userDetails;
    private JsonObject person;
    private JsonObject courses;

    @PostConstruct
    public void configuration() {
        ApplicationConfiguration config = new ApplicationConfiguration(baseUrl, oauthConsumerKey, oauthConsumerSecret, callbackUrl);
        try {
            client = new FenixEduClientImpl(config);
        } catch (
                FenixEduClientException e) {
            throw new TutorException(FENIX_CONFIGURATION_ERROR);
        }
    }

    public void authenticate(FenixAuthenticationDto authentication) {
        try {
            userDetails = client.getUserDetailsFromCode(authentication.getCode());
        } catch (Exception e) {
            throw new TutorException(FENIX_ERROR);
        }

        person = client.getPerson(userDetails.getAuthorization());
    }

    public String getPersonName() {
        return String.valueOf(person.get("name")).replaceAll("^\"|\"$", "");
    }

    public String getPersonUsername() {
        return String.valueOf(person.get("name")).replaceAll("^\"|\"$", "");
    }

    private JsonObject getPersonCourses() {
        return client.getPersonCourses(userDetails.getAuthorization());
    }

    public List<CourseDto> getPersonAttendingCourses() {
        if (courses == null) {
            courses = getPersonCourses();
        }
        return getCourses(courses.get("attending").getAsJsonArray());
    }

    public List<CourseDto> getPersonTeachingCourses() {
        if (courses == null) {
            courses = getPersonCourses();
        }
        return getCourses(courses.get("teaching").getAsJsonArray());
    }

    private List<CourseDto> getCourses(JsonArray coursesJson) {
        List<CourseDto> result = new ArrayList<>();
        for (JsonElement courseJson : coursesJson) {
            result.add(new CourseDto(courseJson.getAsJsonObject().get("name").getAsString(),
                    courseJson.getAsJsonObject().get("acronym").getAsString(),
                    courseJson.getAsJsonObject().get("academicTerm").getAsString()));
        }
        return result;
    }
}
