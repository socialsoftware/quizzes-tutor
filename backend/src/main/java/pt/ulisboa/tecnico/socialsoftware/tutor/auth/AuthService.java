package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.AuthUserDto;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_ENROLLED;

@Service
public class AuthService {
    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AuthDto fenixAuth(FenixEduInterface fenix) {
        String username = fenix.getPersonUsername();
        List<CourseDto> fenixAttendingCourses = fenix.getPersonAttendingCourses();
        List<CourseDto> fenixTeachingCourses = fenix.getPersonTeachingCourses();

        List<CourseExecution> activeAttendingCourses = getActiveTecnicoCourses(fenixAttendingCourses);
        List<CourseExecution> activeTeachingCourses = getActiveTecnicoCourses(fenixTeachingCourses);

        User user = this.userService.findByUsername(username);

        // If user is student and is not in db
        if (user == null && !activeAttendingCourses.isEmpty()) {
            user = this.userService.createUser(fenix.getPersonName(), username, User.Role.STUDENT);
        }

        // If user is teacher and is not in db
        if (user == null && !fenixTeachingCourses.isEmpty()) {
            user = this.userService.createUser(fenix.getPersonName(), username, User.Role.TEACHER);
        }

        if (user == null) {
            throw new TutorException(USER_NOT_ENROLLED, username);
        }

        user.setLastAccess(DateHandler.now());

        if (user.getRole() == User.Role.ADMIN) {
            List<CourseDto> allCoursesInDb = courseExecutionRepository.findAll().stream().map(CourseDto::new).collect(Collectors.toList());

            if (!fenixTeachingCourses.isEmpty()) {
                User finalUser = user;
                activeTeachingCourses.stream().filter(courseExecution -> !finalUser.getCourseExecutions().contains(courseExecution)).forEach(user::addCourse);

                allCoursesInDb.addAll(fenixTeachingCourses);

                String ids = fenixTeachingCourses.stream()
                        .map(courseDto -> courseDto.getAcronym() + courseDto.getAcademicTerm())
                        .collect(Collectors.joining(","));

                user.setEnrolledCoursesAcronyms(ids);
            }
            return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(user,allCoursesInDb));
        }

        // Update student courses
        if (!activeAttendingCourses.isEmpty() && user.getRole() == User.Role.STUDENT) {
            User student = user;
            activeAttendingCourses.stream().filter(courseExecution -> !student.getCourseExecutions().contains(courseExecution)).forEach(user::addCourse);
            return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(user));
        }

        // Update teacher courses
        if (!fenixTeachingCourses.isEmpty() && user.getRole() == User.Role.TEACHER) {
            User teacher = user;
            activeTeachingCourses.stream().filter(courseExecution -> !teacher.getCourseExecutions().contains(courseExecution)).forEach(user::addCourse);

            String ids = fenixTeachingCourses.stream()
                    .map(courseDto -> courseDto.getAcronym() + courseDto.getAcademicTerm())
                    .collect(Collectors.joining(","));

            user.setEnrolledCoursesAcronyms(ids);
            return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(user,  fenixTeachingCourses));
        }

        // Previous teacher without active courses
        if (user.getRole() == User.Role.TEACHER) {
            return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(user));
        }

        throw new TutorException(USER_NOT_ENROLLED, username);
    }

    private List<CourseExecution> getActiveTecnicoCourses(List<CourseDto> courses) {
        return courses.stream()
                .map(courseDto ->  {
                    Course course = courseRepository.findByNameType(courseDto.getName(), Course.Type.TECNICO.name()).orElse(null);
                    if (course == null) {
                        return null;
                    }
                    return course.getCourseExecution(courseDto.getAcronym(),courseDto.getAcademicTerm(), Course.Type.TECNICO)
                                .orElse(null);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            maxAttempts = 2,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AuthDto demoStudentAuth() {
        User user;
//        if (activeProfile.equals("dev")) {
//            user = this.userService.createDemoStudent();
//        } else {
            user = this.userService.getDemoStudent();
//        }

        return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(user));
    }

    @Retryable(
            value = { SQLException.class },
            maxAttempts = 2,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AuthDto demoTeacherAuth() {
        User user = this.userService.getDemoTeacher();

        return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(user));
    }

    @Retryable(
            value = { SQLException.class },
            maxAttempts = 2,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AuthDto demoAdminAuth() {
        User user = this.userService.getDemoAdmin();

        return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(user));
    }
}