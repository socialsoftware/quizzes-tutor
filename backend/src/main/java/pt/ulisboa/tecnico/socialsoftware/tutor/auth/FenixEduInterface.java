package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.sdk.ApplicationConfiguration;
import org.fenixedu.sdk.FenixEduClientImpl;
import org.fenixedu.sdk.FenixEduUserDetails;
import org.fenixedu.sdk.exception.FenixEduClientException;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.FENIX_CONFIGURATION_ERROR;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.FENIX_ERROR;


public class FenixEduInterface {

    private static final String API_TEACHING = "teaching";
    private static final String API_ATTENDING = "attending";

    private static final String API_PERSON_EMAIL = "email";
    private static final String API_PERSON_USERNAME = "username";
    private static final String API_PERSON_NAME = "name";
    private static final String API_PERSON_CURRENT_TERM = "currentAcademicTerm";

    private static final String API_COURSE_ID = "id";
    private static final String API_COURSE_NAME = "name";
    private static final String API_COURSE_ACADEMIC_TERM = "academicTerm";
    private static final String API_COURSE_ACRONYM = "acronym";

    private static final String API_EVALUATION_PERIOD = "evaluationPeriod";
    private static final String API_EVALUATION_END = "end";
    public static final String REGEX = "^\"|\"$";

    private FenixEduClientImpl client;
    private FenixEduUserDetails userDetails;
    private JsonObject person;
    private JsonObject courses;

    public FenixEduInterface(String baseUrl, String oauthConsumerId, String oauthConsumerSecret, String callbackUrl) {
        ApplicationConfiguration config = new ApplicationConfiguration(baseUrl, oauthConsumerId, oauthConsumerSecret, callbackUrl);
        try {
            client = new FenixEduClientImpl(config);
        } catch (
                FenixEduClientException e) {
            throw new TutorException(FENIX_CONFIGURATION_ERROR);
        }
    }

    public void authenticate(String code) {
        try {
            userDetails = client.getUserDetailsFromCode(code);
        } catch (Exception e) {
            throw new TutorException(FENIX_ERROR);
        }

        person = client.getPerson(userDetails.getAuthorization());
    }

    public String getPersonName() {
        return String.valueOf(person.get(API_PERSON_NAME)).replaceAll(REGEX, "");
    }

    public String getPersonUsername() {
        return String.valueOf(person.get(API_PERSON_USERNAME)).replaceAll(REGEX, "");
    }

    private JsonObject getPersonCourses() {
        String academicTerm = client.getAbout().get(API_PERSON_CURRENT_TERM).getAsString();
        Matcher currentYearMatcher = Pattern.compile("([0-9]+/[0-9]+)").matcher(academicTerm);
        currentYearMatcher.find();
        String currentYear = currentYearMatcher.group(1);
        return client.getPersonCourses(userDetails.getAuthorization(), currentYear);
    }

    public List<CourseExecutionDto> getPersonAttendingCourses() {
        if (courses == null) {
            courses = getPersonCourses();
        }
        return getCourses(API_ATTENDING);
    }

    public List<CourseExecutionDto> getPersonTeachingCourses() {
        if (courses == null) {
            courses = getPersonCourses();
        }
        return getCourses(API_TEACHING);
    }

    public String getPersonEmail() {
        return String.valueOf(person.get(API_PERSON_EMAIL)).replaceAll(REGEX, "");
    }

    private String getCourseEndDate(JsonElement courseJson) {
        String id = courseJson.getAsJsonObject().get(API_COURSE_ID).getAsString();
        JsonArray evaluations = client.getCourseEvaluations(id).getAsJsonArray();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime lastDate = null;
        for (JsonElement evaluation : evaluations) {
            String endDate = evaluation.getAsJsonObject()
                                        .get(API_EVALUATION_PERIOD).getAsJsonObject()
                                        .get(API_EVALUATION_END).getAsString();
            if (!endDate.isEmpty()) {
                LocalDateTime evaluationEnd = LocalDateTime.parse(endDate, formatter);
                if (lastDate == null || evaluationEnd.isAfter(lastDate)) {
                    lastDate = evaluationEnd;
                }
            }             
        }
        return DateHandler.toISOString(lastDate);
    }

    private List<CourseExecutionDto> getCourses(String type) {
        JsonArray coursesJson = courses.get(type).getAsJsonArray();
        List<CourseExecutionDto> result = new ArrayList<>();
        for (JsonElement courseJson : coursesJson) {
            CourseExecutionDto course = new CourseExecutionDto(courseJson.getAsJsonObject().get(API_COURSE_NAME).getAsString(),
                    courseJson.getAsJsonObject().get(API_COURSE_ACRONYM).getAsString(),
                    courseJson.getAsJsonObject().get(API_COURSE_ACADEMIC_TERM).getAsString());
            if (type.equals(API_TEACHING)) {
                course.setEndDate(getCourseEndDate(courseJson));
            }
            result.add(course);
        }
        return result;
    }

}
