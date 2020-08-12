package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class AuthService {
    @Autowired
    private UserService userService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto fenixAuth(FenixEduInterface fenix) {
        String username = fenix.getPersonUsername();
        List<CourseDto> fenixAttendingCourses = fenix.getPersonAttendingCourses();
        List<CourseDto> fenixTeachingCourses = fenix.getPersonTeachingCourses();

        List<CourseExecution> activeAttendingCourses = getActiveTecnicoCourses(fenixAttendingCourses);
        List<CourseExecution> activeTeachingCourses = getActiveTecnicoCourses(fenixTeachingCourses);

        User user = this.userService.findByUsername(username);

        // If user is student and is not in db
        if (user == null && !activeAttendingCourses.isEmpty()) {
            user = this.userService.createUser(fenix.getPersonName(), username, fenix.getPersonEmail(), User.Role.STUDENT);
        }

        // If user is teacher and is not in db
        if (user == null && !fenixTeachingCourses.isEmpty()) {
            user = this.userService.createUser(fenix.getPersonName(), username, fenix.getPersonEmail(), User.Role.TEACHER);
        }

        if (user == null) {
            throw new TutorException(USER_NOT_ENROLLED, username);
        }

        if (user.getEmail() == null)
            user.setEmail(fenix.getPersonEmail());

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


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto externalUserAuth(String email, String password) {
        User user = userService.findByUsername(email);

        if (user == null) throw new TutorException(EXTERNAL_USER_NOT_FOUND, email);

        if (password== null ||
                !passwordEncoder.matches(password, user.getPassword()))
            throw new TutorException(INVALID_PASSWORD, password);

        user.setLastAccess(DateHandler.now());

        return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(user));
    }


    @Retryable(
            value = { SQLException.class },
            maxAttempts = 2,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto demoStudentAuth(Boolean createNew) {
        User user;

        if (createNew == null)
            throw new TutorException(INVALID_PARAMETERS);

        if (createNew) 
            user = this.userService.createDemoStudent();
        else
            user = this.userService.getDemoStudent();

        return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(user));
    }

    @Retryable(
            value = { SQLException.class },
            maxAttempts = 2,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto demoTeacherAuth() {
        User user = this.userService.getDemoTeacher();

        return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(user));
    }

    @Retryable(
            value = { SQLException.class },
            maxAttempts = 2,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto demoAdminAuth() {
        User user = this.userService.getDemoAdmin();

        return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(user));
    }

    private List<CourseExecution> getActiveTecnicoCourses(List<CourseDto> courses) {
        return courses.stream()
                .map(courseDto ->  {
                    return courseExecutionRepository.findByFields(courseDto.getAcronym(),courseDto.getAcademicTerm(), Course.Type.TECNICO.name())
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}