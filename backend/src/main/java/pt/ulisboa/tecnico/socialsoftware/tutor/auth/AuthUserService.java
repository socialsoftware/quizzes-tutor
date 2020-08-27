package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.Demo;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.dto.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.repository.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.AuthExternalUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.AuthUserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.AuthUserRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class AuthUserService {
    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthUser findAuthUserByUsername(String username) {
        return this.authUserRepository.findAuthUserByUsername(username).orElse(null);
    }

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

        AuthUser authUser = this.findAuthUserByUsername(username);

        // If user is student and is not in db
        if (authUser == null && !activeAttendingCourses.isEmpty()) {
            authUser = this.userService.createUserWithAuth(fenix.getPersonName(), username, fenix.getPersonEmail(), User.Role.STUDENT, AuthUser.Type.TECNICO);
        }

        // If user is teacher and is not in db
        if (authUser == null && !fenixTeachingCourses.isEmpty()) {
            authUser = this.userService.createUserWithAuth(fenix.getPersonName(), username, fenix.getPersonEmail(), User.Role.TEACHER, AuthUser.Type.TECNICO);
        }

        if (authUser == null) {
            throw new TutorException(USER_NOT_ENROLLED, username);
        }

        if (authUser.getEmail() == null) {
            authUser.setEmail(fenix.getPersonEmail());
        }
        authUser.setLastAccess(DateHandler.now());

        if (authUser.getUser().getRole() == User.Role.ADMIN) {
            List<CourseDto> allCoursesInDb = courseExecutionRepository.findAll().stream().map(CourseDto::new).collect(Collectors.toList());

            if (!fenixTeachingCourses.isEmpty()) {
                User finalUser = authUser.getUser();
                activeTeachingCourses.stream()
                        .filter(courseExecution ->
                                !finalUser.getCourseExecutions().contains(courseExecution))
                        .forEach(authUser.getUser()::addCourse);

                allCoursesInDb.addAll(fenixTeachingCourses);

                String ids = fenixTeachingCourses.stream()
                        .map(courseDto -> courseDto.getAcronym() + courseDto.getAcademicTerm())
                        .collect(Collectors.joining(","));

                authUser.setEnrolledCoursesAcronyms(ids);
            }
            return new AuthDto(JwtTokenProvider.generateToken(authUser.getUser()), new AuthUserDto(authUser, allCoursesInDb));
        }

        // Update student courses
        if (!activeAttendingCourses.isEmpty() && authUser.getUser().getRole() == User.Role.STUDENT) {
            User student = authUser.getUser();
            activeAttendingCourses.stream()
                    .filter(courseExecution ->
                            !student.getCourseExecutions().contains(courseExecution))
                    .forEach(authUser.getUser()::addCourse);
            return new AuthDto(JwtTokenProvider.generateToken(authUser.getUser()), new AuthUserDto(authUser));
        }

        // Update teacher courses
        if (!fenixTeachingCourses.isEmpty() && authUser.getUser().getRole() == User.Role.TEACHER) {
            User teacher = authUser.getUser();
            activeTeachingCourses.stream()
                    .filter(courseExecution ->
                            !teacher.getCourseExecutions().contains(courseExecution))
                    .forEach(authUser.getUser()::addCourse);

            String ids = fenixTeachingCourses.stream()
                    .map(courseDto -> courseDto.getAcronym() + courseDto.getAcademicTerm())
                    .collect(Collectors.joining(","));

            authUser.setEnrolledCoursesAcronyms(ids);
            return new AuthDto(JwtTokenProvider.generateToken(authUser.getUser()), new AuthUserDto(authUser, fenixTeachingCourses));
        }

        // Previous teacher without active courses
        if (authUser.getUser().getRole() == User.Role.TEACHER) {
            return new AuthDto(JwtTokenProvider.generateToken(authUser.getUser()), new AuthUserDto(authUser));
        }

        throw new TutorException(USER_NOT_ENROLLED, username);
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto externalUserAuth(String email, String password) {
        AuthUser authUser = this.findAuthUserByUsername(email);
        if (authUser == null) throw new TutorException(EXTERNAL_USER_NOT_FOUND, email);

        if (password == null ||
                !passwordEncoder.matches(password, authUser.getPassword()))
            throw new TutorException(INVALID_PASSWORD, password);

        authUser.setLastAccess(DateHandler.now());

        return new AuthDto(JwtTokenProvider.generateToken(authUser.getUser()), new AuthUserDto(authUser));
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
            authUser = getDemoStudent(this.userService);
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
        AuthUser authUser = getDemoTeacher(this.userService);

        return new AuthDto(JwtTokenProvider.generateToken(authUser.getUser()), new AuthUserDto(authUser));
    }

    @Retryable(
            value = { SQLException.class },
            maxAttempts = 2,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto demoAdminAuth() {
        AuthUser authUser = getDemoAdmin(this.userService);

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
        AuthExternalUser authUser = (AuthExternalUser) this.findAuthUserByUsername(externalUserDto.getUsername());

        if (authUser == null) {
            throw new TutorException(EXTERNAL_USER_NOT_FOUND, externalUserDto.getUsername());
        }
        if (authUser.isActive()) {
            throw new TutorException(USER_ALREADY_ACTIVE, externalUserDto.getUsername());
        }
        if (externalUserDto.getPassword() == null || externalUserDto.getPassword().isEmpty()) {
            throw new TutorException(INVALID_PASSWORD);
        }

        try {
            authUser.checkConfirmationToken(externalUserDto.getConfirmationToken());
        }
        catch (TutorException e) {
            if (e.getErrorMessage().equals(ErrorMessage.EXPIRED_CONFIRMATION_TOKEN)) {
                userService.generateConfirmationToken(authUser.getUser().getAuthUser());
                return new ExternalUserDto(authUser);
            }
            else throw new TutorException(e.getErrorMessage());
        }

        authUser.setPassword(passwordEncoder.encode(externalUserDto.getPassword()));
        authUser.setActive(true);

        return new ExternalUserDto(authUser);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthUser getDemoTeacher(UserService userService) {
        return authUserRepository.findAuthUserByUsername(Demo.TEACHER_USERNAME).orElseGet(() -> {
            AuthUser authUser = userService.createUserWithAuth("Demo Teacher", Demo.TEACHER_USERNAME, "demo_teacher@mail.com",  User.Role.TEACHER, AuthUser.Type.DEMO);
            authUser.getUser().addCourse(courseService.getDemoCourseExecution());
            return authUser;
        });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthUser getDemoStudent(UserService userService) {
        return authUserRepository.findAuthUserByUsername(Demo.STUDENT_USERNAME).orElseGet(() -> {
            AuthUser authUser = userService.createUserWithAuth("Demo Student", Demo.STUDENT_USERNAME, "demo_student@mail.com", User.Role.STUDENT, AuthUser.Type.DEMO);
            authUser.getUser().addCourse(courseService.getDemoCourseExecution());
            return authUser;
        });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthUser getDemoAdmin(UserService userService) {
        return authUserRepository.findAuthUserByUsername(Demo.ADMIN_USERNAME).orElseGet(() -> {
            AuthUser authUser = userService.createUserWithAuth("Demo Admin", Demo.ADMIN_USERNAME, "demo_admin@mail.com", User.Role.DEMO_ADMIN, AuthUser.Type.DEMO);
            authUser.getUser().addCourse(courseService.getDemoCourseExecution());
            return authUser;
        });
    }
}