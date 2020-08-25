package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.dto.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.repository.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.AuthUserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto;

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
        AuthUser authUser = null;
        if (user != null) {
             authUser = user.getAuthUser();
        }

        // If user is student and is not in db
        if (user == null && !activeAttendingCourses.isEmpty()) {
            authUser = this.userService.createUserWithAuth(fenix.getPersonName(), username, fenix.getPersonEmail(), User.Role.STUDENT);
        }

        // If user is teacher and is not in db
        if (user == null && !fenixTeachingCourses.isEmpty()) {
            authUser = this.userService.createUserWithAuth(fenix.getPersonName(), username, fenix.getPersonEmail(), User.Role.TEACHER);
        }

        if (authUser == null) {
            throw new TutorException(USER_NOT_ENROLLED, username);
        }

        user = authUser.getUser();

        if (authUser.getEmail() == null) {
            authUser.setEmail(fenix.getPersonEmail());
        }
        authUser.setLastAccess(DateHandler.now());

        if (user.getRole() == User.Role.ADMIN) {
            List<CourseDto> allCoursesInDb = courseExecutionRepository.findAll().stream().map(CourseDto::new).collect(Collectors.toList());

            if (!fenixTeachingCourses.isEmpty()) {
                User finalUser = authUser.getUser();
                activeTeachingCourses.stream().filter(courseExecution -> !finalUser.getCourseExecutions().contains(courseExecution)).forEach(user::addCourse);

                allCoursesInDb.addAll(fenixTeachingCourses);

                String ids = fenixTeachingCourses.stream()
                        .map(courseDto -> courseDto.getAcronym() + courseDto.getAcademicTerm())
                        .collect(Collectors.joining(","));

                authUser.setEnrolledCoursesAcronyms(ids);
            }
            return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(authUser, allCoursesInDb));
        }

        // Update student courses
        if (!activeAttendingCourses.isEmpty() && user.getRole() == User.Role.STUDENT) {
            User student = user;
            activeAttendingCourses.stream().filter(courseExecution -> !student.getCourseExecutions().contains(courseExecution)).forEach(user::addCourse);
            return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(authUser));
        }

        // Update teacher courses
        if (!fenixTeachingCourses.isEmpty() && user.getRole() == User.Role.TEACHER) {
            User teacher = user;
            activeTeachingCourses.stream().filter(courseExecution -> !teacher.getCourseExecutions().contains(courseExecution)).forEach(user::addCourse);

            String ids = fenixTeachingCourses.stream()
                    .map(courseDto -> courseDto.getAcronym() + courseDto.getAcademicTerm())
                    .collect(Collectors.joining(","));

            authUser.setEnrolledCoursesAcronyms(ids);
            return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(authUser, fenixTeachingCourses));
        }

        // Previous teacher without active courses
        if (user.getRole() == User.Role.TEACHER) {
            return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(authUser));
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

        AuthUser authUser = user.getAuthUser();

        if (password == null ||
                !passwordEncoder.matches(password, authUser.getPassword()))
            throw new TutorException(INVALID_PASSWORD, password);

        authUser.setLastAccess(DateHandler.now());

        return new AuthDto(JwtTokenProvider.generateToken(user), new AuthUserDto(authUser));
    }


    @Retryable(
            value = { SQLException.class },
            maxAttempts = 2,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto demoStudentAuth(Boolean createNew) {
        AuthUser authUser;
        User user;

        if (createNew == null || !createNew)
            authUser = this.userService.getDemoStudent().getAuthUser();
        else
            authUser = this.userService.createDemoStudent();

        return new AuthDto(JwtTokenProvider.generateToken(authUser.getUser()), new AuthUserDto(authUser));
    }

    @Retryable(
            value = { SQLException.class },
            maxAttempts = 2,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto demoTeacherAuth() {
        AuthUser authUser = this.userService.getDemoTeacher().getAuthUser();

        return new AuthDto(JwtTokenProvider.generateToken(authUser.getUser()), new AuthUserDto(authUser));
    }

    @Retryable(
            value = { SQLException.class },
            maxAttempts = 2,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto demoAdminAuth() {
        AuthUser authUser = this.userService.getDemoAdmin().getAuthUser();

        return new AuthDto(JwtTokenProvider.generateToken(authUser.getUser()), new AuthUserDto(authUser));
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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ExternalUserDto confirmRegistrationTransactional(ExternalUserDto externalUserDto) {
        User user = userService.findByUsername(externalUserDto.getUsername());

        if (user == null)
            throw new TutorException(EXTERNAL_USER_NOT_FOUND, externalUserDto.getUsername());

        AuthUser authUser = user.getAuthUser();

        if (authUser.isActive())
            throw new TutorException(USER_ALREADY_ACTIVE, externalUserDto.getUsername());

        if (externalUserDto.getPassword() == null || externalUserDto.getPassword().isEmpty())
            throw new TutorException(INVALID_PASSWORD);

        try {
            authUser.checkConfirmationToken(externalUserDto.getConfirmationToken());
        }
        catch (TutorException e) {
            if (e.getErrorMessage().equals(ErrorMessage.EXPIRED_CONFIRMATION_TOKEN)) {
                userService.generateConfirmationToken(user.getAuthUser());
                return new ExternalUserDto(user);
            }
            else throw new TutorException(e.getErrorMessage());
        }

        authUser.setPassword(passwordEncoder.encode(externalUserDto.getPassword()));
        authUser.setActive(true);

        return new ExternalUserDto(authUser);
    }
}