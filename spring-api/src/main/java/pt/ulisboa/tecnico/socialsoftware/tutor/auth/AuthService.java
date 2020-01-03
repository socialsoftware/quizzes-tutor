package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.AuthUserDto;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.*;

@Service
public class AuthService {
    @Autowired
    private UserService userService;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Retryable(
            value = { SQLException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AuthDto fenixAuth(FenixEduInterface client) {
        String username = client.getPersonUsername();
        List<CourseDto> attendingCourses = client.getPersonAttendingCourses();
        List<CourseDto> teachingCourses = client.getPersonTeachingCourses();

        Set<CourseExecution> activeAttendingCourses = getActiveCourses(attendingCourses);
        Set<CourseExecution> activeTeachingCourses = getActiveCourses(teachingCourses);

        User user = this.userService.findByUsername(username);

        // If user is student not in db
        if (user == null && !activeAttendingCourses.isEmpty()) {
            user = this.userService.createUser(client.getPersonName(), username, User.Role.STUDENT);
        }

        // If user is teacher not in db
        if (user == null && !activeTeachingCourses.isEmpty()) {
            user = this.userService.createUser(client.getPersonName(), username, User.Role.TEACHER);
        }

        // Update student courses
        if (!activeAttendingCourses.isEmpty()) {
            User student = user;
            activeAttendingCourses.stream().filter(courseExecution -> !student.getCourseExecutions().contains(courseExecution)).forEach(user::addCourse);
            return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(user));
        }

        // Update teacher courses
        if (!activeTeachingCourses.isEmpty()) {
            User teacher = user;
            activeTeachingCourses.stream().filter(courseExecution -> !teacher.getCourseExecutions().contains(courseExecution)).forEach(user::addCourse);

            String acronyms = teachingCourses.stream()
                    .map(CourseDto::getAcronym)
                    .collect(Collectors.joining(","));

            user.setCourseExecutionAcronyms(acronyms);
            return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(user, getOtherCourses(teachingCourses) ));
        }

        if (user != null && user.getRole() == User.Role.ADMIN) {
            return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(user));
        }

        throw new TutorException(USER_NOT_ENROLLED, username);
    }

    private Set<CourseExecution> getActiveCourses(List<CourseDto> courses) {
        return courses.stream()
                .map(courseDto -> courseExecutionRepository.findByAcronym(courseDto.getAcronym()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private List<CourseDto> getOtherCourses(List<CourseDto> courses) {
        return courses.stream()
                .filter(courseDto -> courseExecutionRepository.findByAcronym(courseDto.getAcronym()) == null)
                .collect(Collectors.toList());
    }
}