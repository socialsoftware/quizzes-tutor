package pt.ulisboa.tecnico.socialsoftware.apigateway.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.apigateway.auth.domain.AuthExternalUser;
import pt.ulisboa.tecnico.socialsoftware.apigateway.auth.domain.AuthTecnicoUser;
import pt.ulisboa.tecnico.socialsoftware.apigateway.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.apigateway.auth.domain.UserSecurityInfo;
import pt.ulisboa.tecnico.socialsoftware.apigateway.auth.dto.AuthDto;
import pt.ulisboa.tecnico.socialsoftware.apigateway.auth.dto.AuthUserDto;
import pt.ulisboa.tecnico.socialsoftware.apigateway.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.course.CourseType;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.ExternalUserDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.Notification;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.NotificationResponse;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.common.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.demoutils.TutorDemoUtils;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.*;

@Service
public class AuthUserService {

    public static final String MAIL_FORMAT = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    @Autowired
    private UserService userService;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CourseRepository courseRepository;

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

        List<CourseExecutionDto> courseExecutionList = getCourseExecutions(authUser);

        if (authUser.getUserSecurityInfo().isTeacher()) {
            return new AuthDto(JwtTokenProvider.generateToken(authUser), new AuthUserDto(authUser, fenix.getPersonTeachingCourses(), courseExecutionList));
        } else {
            return new AuthDto(JwtTokenProvider.generateToken(authUser), new AuthUserDto(authUser, courseExecutionList));
        }
    }

    private List<CourseExecutionDto> getCourseExecutions(AuthUser authUser) {
        Integer userId = authUser.getUserSecurityInfo().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        return user.getCourseExecutions().stream().map(CourseExecution::getDto).collect(Collectors.toList());
    }

    private void refreshFenixAuthUserInfo(FenixEduInterface fenix, AuthTecnicoUser authUser) {
        authUser.setEmail(fenix.getPersonEmail());
        if (authUser.getUserSecurityInfo().isTeacher()) {
            List<CourseExecutionDto> fenixTeachingCourses = fenix.getPersonTeachingCourses();
            updateTeacherCourses(authUser, fenixTeachingCourses);
        } else {
            List<CourseExecutionDto> fenixAttendingCourses = fenix.getPersonAttendingCourses();
            updateStudentCourses(authUser, fenixAttendingCourses);
        }
    }

    private void updateTeacherCourses(AuthTecnicoUser authUser, List<CourseExecutionDto> fenixTeachingCourses) {
        List<CourseExecution> activeTeachingCourses = getActiveTecnicoCourses(fenixTeachingCourses);
        if (!fenixTeachingCourses.isEmpty() && authUser.getUserSecurityInfo().isTeacher()) {
            User teacher = userRepository.findById(authUser.getUserSecurityInfo().getId())
                    .orElseThrow(() -> new TutorException(USER_NOT_FOUND, authUser.getUserSecurityInfo().getId()));

            activeTeachingCourses.stream()
                    .filter(courseExecution ->
                            !teacher.getCourseExecutions().contains(courseExecution))
                    .forEach(courseExecution -> {
                        teacher.addCourse(courseExecution);
                        authUser.addCourseExecution(courseExecution.getId());
                    });

            String ids = fenixTeachingCourses.stream()
                    .map(courseDto -> courseDto.getAcronym() + courseDto.getAcademicTerm())
                    .collect(Collectors.joining(","));

            authUser.setEnrolledCoursesAcronyms(ids);
        }
    }

    private void updateStudentCourses(AuthTecnicoUser authUser, List<CourseExecutionDto> fenixAttendingCourses) {
        List<CourseExecution> activeAttendingCourses = getActiveTecnicoCourses(fenixAttendingCourses);
        if (!activeAttendingCourses.isEmpty() && authUser.getUserSecurityInfo().isStudent()) {
            User student = userRepository.findById(authUser.getUserSecurityInfo().getId())
                    .orElseThrow(() -> new TutorException(USER_NOT_FOUND, authUser.getUserSecurityInfo().getId()));

            activeAttendingCourses.stream()
                    .filter(courseExecution ->
                            !student.getCourseExecutions().contains(courseExecution))
                    .forEach(courseExecution -> {
                        student.addCourse(courseExecution);
                        authUser.addCourseExecution(courseExecution.getId());
                    });
        }
    }
//TODO:Add course executions
    private AuthTecnicoUser createAuthUser(FenixEduInterface fenix, String username) {
        List<CourseExecutionDto> fenixAttendingCourses = fenix.getPersonAttendingCourses();
        List<CourseExecutionDto> fenixTeachingCourses = fenix.getPersonTeachingCourses();

        List<CourseExecution> activeAttendingCourses = getActiveTecnicoCourses(fenixAttendingCourses);

        AuthTecnicoUser authUser = null;

        // If user is student and is not in db
        if (!activeAttendingCourses.isEmpty()) {
            authUser = (AuthTecnicoUser) createUserWithAuth(fenix.getPersonName(), username, fenix.getPersonEmail(), Role.STUDENT, AuthUser.Type.TECNICO);
            updateStudentCourses(authUser, fenixAttendingCourses);
        }

        // If user is teacher and is not in db
        if (!fenixTeachingCourses.isEmpty()) {
            authUser = (AuthTecnicoUser) createUserWithAuth(fenix.getPersonName(), username, fenix.getPersonEmail(), Role.TEACHER, AuthUser.Type.TECNICO);
            updateTeacherCourses(authUser, fenixTeachingCourses);
        }

        if (authUser == null) {
            throw new TutorException(USER_NOT_ENROLLED, username);
        }
        return authUser;
    }

    //Was from user service
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthUser createUserWithAuth(String name, String username, String email, Role role, AuthUser.Type type) {
        if (authUserRepository.findAuthUserByUsername(username).isPresent()) {
            throw new TutorException(DUPLICATE_USER, username);
        }

        User user = new User(name, username, email, role, false);
        user.setActive(type != AuthUser.Type.EXTERNAL);
        userRepository.save(user);

        AuthUser authUser = AuthUser.createAuthUser(new UserSecurityInfo(user.getId(), name, role, false), username, email, type);
        authUserRepository.save(authUser);
        return authUser;
    }


    //Was from user service
    @Transactional(isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED)
    public ExternalUserDto registerExternalUserTransactional(Integer courseExecutionId, ExternalUserDto externalUserDto) {
        CourseExecution courseExecution = getExternalCourseExecution(courseExecutionId);
        AuthExternalUser authUser = getOrCreateUser(externalUserDto);

        if (authUser.getUserCourseExecutions().contains(courseExecutionId)) {
            throw new TutorException(DUPLICATE_USER, authUser.getUsername());
        }

        User user = userRepository.findById(authUser.getUserSecurityInfo().getId())
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, authUser.getUserSecurityInfo().getId()));
        user.addCourse(courseExecution);
        authUser.addCourseExecution(courseExecutionId);
        authUser.generateConfirmationToken();
        return authUser.getDto();
    }

    //Was from user service
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ExternalUserDto confirmRegistrationTransactional(ExternalUserDto externalUserDto) {
        AuthExternalUser authUser = (AuthExternalUser) authUserRepository.findAuthUserByUsername(externalUserDto.getUsername()).orElse(null);

        if (authUser == null) {
            throw new TutorException(EXTERNAL_USER_NOT_FOUND, externalUserDto.getUsername());
        }

        if (externalUserDto.getPassword() == null || externalUserDto.getPassword().isEmpty()) {
            throw new TutorException(INVALID_PASSWORD);
        }

        try {
            authUser.confirmRegistration(passwordEncoder, externalUserDto.getConfirmationToken(),
                    externalUserDto.getPassword());

            User user = userRepository.findById(authUser.getUserSecurityInfo().getId())
                    .orElseThrow(() -> new TutorException(USER_NOT_FOUND, authUser.getUserSecurityInfo().getId()));
            user.setActive(true);
        }
        catch (TutorException e) {
            if (e.getErrorMessage().equals(ErrorMessage.EXPIRED_CONFIRMATION_TOKEN)) {
                authUser.generateConfirmationToken();
            }
            else throw new TutorException(e.getErrorMessage());
        }

        return authUser.getDto();
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
        List<CourseExecutionDto> courseExecutionList = getCourseExecutions(authUser);
        return new AuthDto(JwtTokenProvider.generateToken(authUser), new AuthUserDto(authUser,courseExecutionList));
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
            authUser = createDemoStudent();
        }
        List<CourseExecutionDto> courseExecutionList = getCourseExecutions(authUser);
        return new AuthDto(JwtTokenProvider.generateToken(authUser), new AuthUserDto(authUser, courseExecutionList));
    }

    //Was from user service
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AuthUser createDemoStudent() {
        String birthDate = LocalDateTime.now().toString() + new Random().nextDouble();
        AuthUser authUser = createUserWithAuth("Demo-Student-" + birthDate, "Demo-Student-" + birthDate, "demo_student@mail.com", Role.STUDENT, AuthUser.Type.DEMO);
        CourseExecution courseExecution = courseExecutionService.getDemoCourseExecution();
        User user = userRepository.findById(authUser.getUserSecurityInfo().getId())
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, authUser.getUserSecurityInfo().getId()));
        if (courseExecution != null) {
            courseExecution.addUser(user);
            user.addCourse(courseExecution);
            authUser.addCourseExecution(courseExecution.getId());
        }
        return authUser;
    }

    @Retryable(
            value = { SQLException.class },
            maxAttempts = 2,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto demoTeacherAuth() {
        AuthUser authUser = getDemoTeacher();
        List<CourseExecutionDto> courseExecutionList = getCourseExecutions(authUser);
        return new AuthDto(JwtTokenProvider.generateToken(authUser), new AuthUserDto(authUser, courseExecutionList));
    }

    @Retryable(
            value = { SQLException.class },
            maxAttempts = 2,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto demoAdminAuth() {
        AuthUser authUser = getDemoAdmin();
        List<CourseExecutionDto> courseExecutionList = getCourseExecutions(authUser);
        return new AuthDto(JwtTokenProvider.generateToken(authUser), new AuthUserDto(authUser, courseExecutionList));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String getEnrolledCoursesAcronyms(int userId) {
        AuthUser authUser = authUserRepository.findAuthUserById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        return authUser.getEnrolledCoursesAcronyms();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean userHasAnExecutionOfCourse(int userId, int courseId) {
        return courseRepository.findCourseWithCourseExecutionsById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId))
                .getCourseExecutions()
                .stream()
                .anyMatch(courseExecution ->  authUserRepository.countUserCourseExecutionsPairById(userId, courseExecution.getId()) == 1);
    }

    //Was from user service
    @Transactional(isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED)
    public NotificationResponse<CourseExecutionDto> registerListOfUsersTransactional(InputStream stream, int courseExecutionId) {
        Notification notification = new Notification();
        extractUserDtos(stream, notification).forEach(userDto ->
                registerExternalUserTransactional(courseExecutionId, userDto)
        );

        CourseExecutionDto courseExecutionDto = courseExecutionRepository.findById(courseExecutionId)
                .map(CourseExecution::getDto)
                .get();

        return new NotificationResponse<>(notification, courseExecutionDto);
    }

    //Was from user service
    private List<ExternalUserDto> extractUserDtos(InputStream stream, Notification notification) {
        List<ExternalUserDto> userDtos = new ArrayList<>();
        String line = "";
        String cvsSplitBy = ",";
        Role auxRole;
        int lineNumber = 1;
        InputStreamReader isr = new InputStreamReader(stream, StandardCharsets.UTF_8);
        try (BufferedReader br = new BufferedReader(isr)) {
            while ((line = br.readLine()) != null) {
                String[] userInfo = line.split(cvsSplitBy);
                if (userInfo.length == 3) {
                    auxRole = Role.STUDENT;
                } else if (userInfo.length == 4 && (userInfo[3].equalsIgnoreCase("student")
                        || userInfo[3].equalsIgnoreCase("teacher"))) {
                    auxRole = Role.valueOf(userInfo[3].toUpperCase());
                } else {
                    notification.addError(String.format(WRONG_FORMAT_ON_CSV_LINE.label, lineNumber),
                            new TutorException(INVALID_CSV_FILE_FORMAT));
                    lineNumber++;
                    continue;
                }

                if (userInfo[0].length() == 0 || !userInfo[0].matches(MAIL_FORMAT) || userInfo[1].length() == 0) {
                    notification.addError(String.format(WRONG_FORMAT_ON_CSV_LINE.label, lineNumber),
                            new TutorException(INVALID_CSV_FILE_FORMAT));
                    lineNumber++;
                    continue;
                }

                ExternalUserDto userDto = new ExternalUserDto();
                userDto.setEmail(userInfo[0]);
                userDto.setUsername(userInfo[1]);
                userDto.setName(userInfo[2]);
                userDto.setRole(auxRole);
                userDtos.add(userDto);
                lineNumber++;
            }
        } catch (IOException ex) {
            throw new TutorException(CANNOT_OPEN_FILE);
        }

        if (notification.hasErrors())
            return new ArrayList<>();

        return userDtos;
    }

    private List<CourseExecution> getActiveTecnicoCourses(List<CourseExecutionDto> courses) {
        return courses.stream()
                .map(courseDto -> courseExecutionRepository.findByFields(courseDto.getAcronym(),courseDto.getAcademicTerm(), CourseType.TECNICO.name())
                        .orElse(null)
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private AuthUser getDemoTeacher() {
        return authUserRepository.findAuthUserByUsername(TutorDemoUtils.TEACHER_USERNAME).orElseGet(() -> {
            AuthUser authUser = createUserWithAuth("Demo Teacher", TutorDemoUtils.TEACHER_USERNAME, "demo_teacher@mail.com",  Role.TEACHER, AuthUser.Type.DEMO);
            User user = userRepository.findById(authUser.getUserSecurityInfo().getId())
                    .orElseThrow(() -> new TutorException(USER_NOT_FOUND, authUser.getUserSecurityInfo().getId()));
            user.addCourse(courseExecutionService.getDemoCourseExecution());
            authUser.addCourseExecution(courseExecutionService.getDemoCourseExecution().getId());
            return authUser;
        });
    }

    private AuthUser getDemoStudent() {
        return authUserRepository.findAuthUserByUsername(TutorDemoUtils.STUDENT_USERNAME).orElseGet(() -> {
            AuthUser authUser = createUserWithAuth("Demo Student", TutorDemoUtils.STUDENT_USERNAME, "demo_student@mail.com", Role.STUDENT, AuthUser.Type.DEMO);
            User user = userRepository.findById(authUser.getUserSecurityInfo().getId())
                    .orElseThrow(() -> new TutorException(USER_NOT_FOUND, authUser.getUserSecurityInfo().getId()));
            user.addCourse(courseExecutionService.getDemoCourseExecution());
            authUser.addCourseExecution(courseExecutionService.getDemoCourseExecution().getId());
            return authUser;
        });
    }

    private AuthUser getDemoAdmin() {
        return authUserRepository.findAuthUserByUsername(TutorDemoUtils.ADMIN_USERNAME).orElseGet(() -> {
            AuthUser authUser = createUserWithAuth("Demo Admin", TutorDemoUtils.ADMIN_USERNAME, "demo_admin@mail.com", Role.DEMO_ADMIN, AuthUser.Type.DEMO);
            User user = userRepository.findById(authUser.getUserSecurityInfo().getId())
                    .orElseThrow(() -> new TutorException(USER_NOT_FOUND, authUser.getUserSecurityInfo().getId()));
            user.addCourse(courseExecutionService.getDemoCourseExecution());
            authUser.addCourseExecution(courseExecutionService.getDemoCourseExecution().getId());
            return authUser;
        });
    }

    private AuthExternalUser getOrCreateUser(ExternalUserDto externalUserDto) {
        //Avoid creation of user and authUser with invalid roles (added because of tests and will be needed for sagas)
        checkRole(externalUserDto.getRole(), externalUserDto.getActive());
        String username = externalUserDto.getUsername() != null ? externalUserDto.getUsername() : externalUserDto.getEmail();
        return (AuthExternalUser) authUserRepository.findAuthUserByUsername(username)
                .orElseGet(() -> {
                    User user = new User(externalUserDto.getName(),
                            username, externalUserDto.getEmail(),
                            externalUserDto.getRole(), false);
                    user.setActive(false);
                    userRepository.save(user);
                    AuthUser authUser = AuthUser.createAuthUser(new UserSecurityInfo(user.getId(),
                                    externalUserDto.getName(), externalUserDto.getRole(), false),
                            username, externalUserDto.getEmail(), AuthUser.Type.EXTERNAL);
                    authUserRepository.save(authUser);
                    return authUser;
                });
    }

    private CourseExecution getExternalCourseExecution(Integer courseExecutionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        if (!courseExecution.getType().equals(CourseType.EXTERNAL)) {
            throw new TutorException(COURSE_EXECUTION_NOT_EXTERNAL, courseExecutionId);
        }
        return courseExecution;
    }

    public void checkRole(Role role, boolean isActive) {
        if (!isActive && role != null && !(role.equals(Role.STUDENT) || role.equals(Role.TEACHER))) {
            throw new TutorException(INVALID_ROLE, role.toString());
        }
    }


    // Was from user service
    /*public String exportUsers() {
        UsersXmlExport xmlExporter = new UsersXmlExport();
        return xmlExporter.export(userRepository.findAll());
    }

    public void importUsers(String usersXML) {
        UsersXmlImport xmlImporter = new UsersXmlImport();
        xmlImporter.importUsers(usersXML, this);
    }*/

}