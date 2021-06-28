package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto.AuthDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthTecnicoUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto.AuthUserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DemoUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class AuthUserService {
    @Autowired
    private UserService userService;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Autowired
    private AuthUserRepository authUserRepository;

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
        AuthTecnicoUser authUser;
        try {
            authUser = (AuthTecnicoUser) authUserRepository.findAuthUserByUsername(username).orElse(null);
        } catch (ClassCastException e) {
            throw new TutorException(INVALID_AUTH_USERNAME, username);
        }

        if (authUser == null) {
            authUser = createAuthUser(fenix, username);
        } else {
            refreshFenixAuthUserInfo(fenix, authUser);
        }

        authUser.setLastAccess(DateHandler.now());

        if (authUser.getUser().isTeacher()) {
            return new AuthDto(JwtTokenProvider.generateToken(authUser), new AuthUserDto(authUser, fenix.getPersonTeachingCourses()));
        } else {
            return new AuthDto(JwtTokenProvider.generateToken(authUser), new AuthUserDto(authUser));
        }
    }

    private void refreshFenixAuthUserInfo(FenixEduInterface fenix, AuthTecnicoUser authUser) {
        authUser.setEmail(fenix.getPersonEmail());
        if (authUser.getUser().isTeacher()) {
            List<CourseExecutionDto> fenixTeachingCourses = fenix.getPersonTeachingCourses();
            updateTeacherCourses(authUser, fenixTeachingCourses);
        } else {
            List<CourseExecutionDto> fenixAttendingCourses = fenix.getPersonAttendingCourses();
            updateStudentCourses(authUser, fenixAttendingCourses);
        }
    }

    private void updateTeacherCourses(AuthTecnicoUser authUser, List<CourseExecutionDto> fenixTeachingCourses) {
        List<CourseExecution> activeTeachingCourses = getActiveTecnicoCourses(fenixTeachingCourses);
        if (!fenixTeachingCourses.isEmpty() && authUser.getUser().isTeacher()) {
            User teacher = authUser.getUser();
            activeTeachingCourses.stream()
                    .filter(courseExecution ->
                            !teacher.getCourseExecutions().contains(courseExecution))
                    .forEach(authUser.getUser()::addCourse);

            String ids = fenixTeachingCourses.stream()
                    .map(courseDto -> courseDto.getAcronym() + courseDto.getAcademicTerm())
                    .collect(Collectors.joining(","));

            authUser.setEnrolledCoursesAcronyms(ids);
        }
    }

    private void updateStudentCourses(AuthTecnicoUser authUser, List<CourseExecutionDto> fenixAttendingCourses) {
        List<CourseExecution> activeAttendingCourses = getActiveTecnicoCourses(fenixAttendingCourses);
        if (!activeAttendingCourses.isEmpty() && authUser.getUser().isStudent()) {
            User student = authUser.getUser();
            activeAttendingCourses.stream()
                    .filter(courseExecution ->
                            !student.getCourseExecutions().contains(courseExecution))
                    .forEach(authUser.getUser()::addCourse);
        }
    }

    private AuthTecnicoUser createAuthUser(FenixEduInterface fenix, String username) {
        List<CourseExecutionDto> fenixAttendingCourses = fenix.getPersonAttendingCourses();
        List<CourseExecutionDto> fenixTeachingCourses = fenix.getPersonTeachingCourses();

        List<CourseExecution> activeAttendingCourses = getActiveTecnicoCourses(fenixAttendingCourses);

        AuthTecnicoUser authUser = null;

        // If user is student and is not in db
        if (!activeAttendingCourses.isEmpty()) {
            authUser = (AuthTecnicoUser) userService.createUserWithAuth(fenix.getPersonName(), username, fenix.getPersonEmail(), User.Role.STUDENT, AuthUser.Type.TECNICO);
            updateStudentCourses(authUser, fenixAttendingCourses);
        }

        // If user is teacher and is not in db
        if (!fenixTeachingCourses.isEmpty()) {
            authUser = (AuthTecnicoUser) userService.createUserWithAuth(fenix.getPersonName(), username, fenix.getPersonEmail(), User.Role.TEACHER, AuthUser.Type.TECNICO);
            updateTeacherCourses(authUser, fenixTeachingCourses);
        }

        if (authUser == null) {
            throw new TutorException(USER_NOT_ENROLLED, username);
        }
        return authUser;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto externalUserAuth(String email, String password) {
        Optional<AuthUser> optionalAuthUser = authUserRepository.findAuthUserByUsername(email);
        if (optionalAuthUser.isEmpty()) {
            throw new TutorException(EXTERNAL_USER_NOT_FOUND, email);
        }

        AuthUser authUser = optionalAuthUser.get();

        if (password == null ||
                !passwordEncoder.matches(password, authUser.getPassword())) {
            throw new TutorException(INVALID_PASSWORD, password);
        }
        authUser.setLastAccess(DateHandler.now());

        return new AuthDto(JwtTokenProvider.generateToken(authUser), new AuthUserDto(authUser));
    }

    @Retryable(
            value = { SQLException.class },
            maxAttempts = 2,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto demoStudentAuth(Boolean createNew) {
        AuthUser authUser;

        if (createNew == null || !createNew) {
            authUser = getDemoStudent();
        }
        else {
            authUser = userService.createDemoStudent();
        }
        return new AuthDto(JwtTokenProvider.generateToken(authUser), new AuthUserDto(authUser));
    }

    @Retryable(
            value = { SQLException.class },
            maxAttempts = 2,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto demoTeacherAuth() {
        AuthUser authUser = getDemoTeacher();

        return new AuthDto(JwtTokenProvider.generateToken(authUser), new AuthUserDto(authUser));
    }

    @Retryable(
            value = { SQLException.class },
            maxAttempts = 2,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto demoAdminAuth() {
        AuthUser authUser = getDemoAdmin();

        return new AuthDto(JwtTokenProvider.generateToken(authUser), new AuthUserDto(authUser));
    }

    private List<CourseExecution> getActiveTecnicoCourses(List<CourseExecutionDto> courses) {
        return courses.stream()
                .map(courseDto -> courseExecutionRepository.findByFields(courseDto.getAcronym(),courseDto.getAcademicTerm(), Course.Type.TECNICO.name())
                            .orElse(null)
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private AuthUser getDemoTeacher() {
        return authUserRepository.findAuthUserByUsername(DemoUtils.TEACHER_USERNAME).orElseGet(() -> {
            AuthUser authUser = userService.createUserWithAuth("Demo Teacher", DemoUtils.TEACHER_USERNAME, "demo_teacher@mail.com",  User.Role.TEACHER, AuthUser.Type.DEMO);
            authUser.getUser().addCourse(courseExecutionService.getDemoCourseExecution());
            return authUser;
        });
    }

    private AuthUser getDemoStudent() {
        return authUserRepository.findAuthUserByUsername(DemoUtils.STUDENT_USERNAME).orElseGet(() -> {
            AuthUser authUser = userService.createUserWithAuth("Demo Student", DemoUtils.STUDENT_USERNAME, "demo_student@mail.com", User.Role.STUDENT, AuthUser.Type.DEMO);
            authUser.getUser().addCourse(courseExecutionService.getDemoCourseExecution());
            return authUser;
        });
    }

    private AuthUser getDemoAdmin() {
        return authUserRepository.findAuthUserByUsername(DemoUtils.ADMIN_USERNAME).orElseGet(() -> {
            AuthUser authUser = userService.createUserWithAuth("Demo Admin", DemoUtils.ADMIN_USERNAME, "demo_admin@mail.com", User.Role.DEMO_ADMIN, AuthUser.Type.DEMO);
            authUser.getUser().addCourse(courseExecutionService.getDemoCourseExecution());
            return authUser;
        });
    }

}