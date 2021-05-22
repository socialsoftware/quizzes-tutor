package pt.ulisboa.tecnico.socialsoftware.auth.services;

import io.eventuate.tram.sagas.orchestration.SagaInstance;
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.auth.apis.AuthController;
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthExternalUser;
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthTecnicoUser;
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.auth.domain.UserSecurityInfo;
import pt.ulisboa.tecnico.socialsoftware.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.createUserWithAuth.CreateUserWithAuthSaga;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.createUserWithAuth.CreateUserWithAuthSagaData;
import pt.ulisboa.tecnico.socialsoftware.auth.services.remote.AuthRequiredService;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.auth.AuthDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.course.CourseType;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.ExternalUserDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.Notification;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.NotificationResponse;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.common.utils.DateHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.common.utils.Utils.*;

@Service
public class AuthUserService {

    private static final Logger logger = LoggerFactory.getLogger(AuthUserService.class);

    @Autowired
    private AuthRequiredService authRequiredService;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SagaInstanceFactory sagaInstanceFactory;

    @Autowired
    private CreateUserWithAuthSaga createUserWithAuthSaga;

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
            throw new TutorException(ErrorMessage.INVALID_AUTH_USERNAME, username);
        }

        if (authUser == null) {
            authUser = createAuthUser(fenix, username);
        } else {
            refreshFenixAuthUserInfo(fenix, authUser);
        }

        authUser.setLastAccess(DateHandler.now());

        List<CourseExecutionDto> courseExecutionList = getCourseExecutions(authUser);

        if (authUser.getUserSecurityInfo().isTeacher()) {
            return authUser.getAuthDto(JwtTokenProvider.generateToken(authUser), fenix.getPersonTeachingCourses(), courseExecutionList);
        } else {
            return authUser.getAuthDto(JwtTokenProvider.generateToken(authUser), null, courseExecutionList);
        }
    }

    private List<CourseExecutionDto> getCourseExecutions(AuthUser authUser) {
        Integer userId = authUser.getUserSecurityInfo().getId();
        return authRequiredService.getUserCourseExecutions(userId);
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
        List<CourseExecutionDto> activeTeachingCourses = getActiveTecnicoCourses(fenixTeachingCourses);
        if (!fenixTeachingCourses.isEmpty() && authUser.getUserSecurityInfo().isTeacher()) {

            activeTeachingCourses.stream()
                    .filter(courseExecutionDto ->
                            !authUser.getUserCourseExecutions().contains(courseExecutionDto.getCourseExecutionId()))
                    .forEach(courseExecutionDto -> {
                        //TODO: fix this
                        //userService.addCourseExecution(authUser.getUserSecurityInfo().getId(), courseExecutionDto.getCourseExecutionId());
                        authUser.addCourseExecution(courseExecutionDto.getCourseExecutionId());
                    });

            String ids = fenixTeachingCourses.stream()
                    .map(courseDto -> courseDto.getAcronym() + courseDto.getAcademicTerm())
                    .collect(Collectors.joining(","));

            authUser.setEnrolledCoursesAcronyms(ids);
        }
    }

    private void updateStudentCourses(AuthTecnicoUser authUser, List<CourseExecutionDto> fenixAttendingCourses) {
        List<CourseExecutionDto> activeAttendingCourses = getActiveTecnicoCourses(fenixAttendingCourses);
        if (!activeAttendingCourses.isEmpty() && authUser.getUserSecurityInfo().isStudent()) {

            activeAttendingCourses.stream()
                    .filter(courseExecutionDto ->
                            !authUser.getUserCourseExecutions().contains(courseExecutionDto.getCourseExecutionId()))
                    .forEach(courseExecutionDto -> {
                        //TODO: fix this
                        //userService.addCourseExecution(authUser.getUserSecurityInfo().getId(), courseExecutionDto.getCourseExecutionId());
                        authUser.addCourseExecution(courseExecutionDto.getCourseExecutionId());
                    });
        }
    }

    private AuthTecnicoUser createAuthUser(FenixEduInterface fenix, String username) {
        List<CourseExecutionDto> fenixAttendingCourses = fenix.getPersonAttendingCourses();
        List<CourseExecutionDto> fenixTeachingCourses = fenix.getPersonTeachingCourses();

        List<CourseExecutionDto> activeAttendingCourses = getActiveTecnicoCourses(fenixAttendingCourses);

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
            throw new TutorException(ErrorMessage.USER_NOT_ENROLLED, username);
        }
        return authUser;
    }

    //Was from user service
    //@Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthUser createUserWithAuth(String name, String username, String email, Role role, AuthUser.Type type) {
        if (authUserRepository.findAuthUserByUsername(username).isPresent()) {
            throw new TutorException(ErrorMessage.DUPLICATE_USER, username);
        }
        //UserDto userDto = userService.createUser(name, role, username, type != AuthUser.Type.EXTERNAL, false);

        /*AuthUser authUser = AuthUser.createAuthUser(new UserSecurityInfo(userDto.getId(), name, role, false), username, email, type);
        authUserRepository.save(authUser);*/
        logger.info("Will create authUser");
        AuthUser authUser = AuthUser.createAuthUser(new UserSecurityInfo(name, role, false), username, email, type);
        authUserRepository.save(authUser);
        logger.info("AuthUserCreated: "+ authUser);
        return authUser;
    }


    //Was from user service
    @Transactional(isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED)
    public ExternalUserDto registerExternalUserTransactional(Integer courseExecutionId, ExternalUserDto externalUserDto) {
        CourseExecutionDto courseExecutionDto = authRequiredService.getCourseExecutionById(courseExecutionId);

        if (!courseExecutionDto.getCourseExecutionType().equals(CourseType.EXTERNAL)) {
            throw new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_EXTERNAL, courseExecutionId);
        }

        AuthExternalUser authUser = getOrCreateUser(externalUserDto);

        if (authUser.getUserCourseExecutions().contains(courseExecutionId)) {
            throw new TutorException(ErrorMessage.DUPLICATE_USER, authUser.getUsername());
        }

        //TODO: fix this
        //courseExecutionService.addUserToTecnicoCourseExecution(authUser.getUserSecurityInfo().getId(), courseExecutionId);
        authUser.generateConfirmationToken();
        return authUser.getDto();
    }

    //Was from user service
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ExternalUserDto confirmRegistrationTransactional(ExternalUserDto externalUserDto) {
        AuthExternalUser authUser = (AuthExternalUser) authUserRepository.findAuthUserByUsername(externalUserDto.getUsername()).orElse(null);

        if (authUser == null) {
            throw new TutorException(ErrorMessage.EXTERNAL_USER_NOT_FOUND, externalUserDto.getUsername());
        }

        if (externalUserDto.getPassword() == null || externalUserDto.getPassword().isEmpty()) {
            throw new TutorException(ErrorMessage.INVALID_PASSWORD);
        }

        try {
            authUser.confirmRegistration(passwordEncoder, externalUserDto.getConfirmationToken(),
                    externalUserDto.getPassword());

            //TODO: fix this
            //userService.activateUser(authUser.getUserSecurityInfo().getId());
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
            throw new TutorException(ErrorMessage.EXTERNAL_USER_NOT_FOUND, email);
        }

        AuthUser authUser = optionalAuthUser.get();

        if (password == null ||
                !passwordEncoder.matches(password, authUser.getPassword())) {
            throw new TutorException(ErrorMessage.INVALID_PASSWORD, password);
        }
        authUser.setLastAccess(DateHandler.now());
        List<CourseExecutionDto> courseExecutionList = getCourseExecutions(authUser);
        return authUser.getAuthDto(JwtTokenProvider.generateToken(authUser), null , courseExecutionList);
    }

    /*@Retryable(
            value = { SQLException.class },
            maxAttempts = 2,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)*/
    public AuthDto demoStudentAuth(Boolean createNew) {
        AuthUser authUser;

        if (createNew == null || !createNew) {
            logger.info("GetDemoStudent");
            authUser = getDemoStudent();
            logger.info("GotDemoStudent: " + authUser);
        }
        else {
            logger.info("CreateDemoStudent");
            authUser = createDemoStudent();
            logger.info("CreatedDemoStudent: " + authUser);
        }

        List<CourseExecutionDto> courseExecutionList = getCourseExecutions(authUser);
        logger.info("courseExecutionList: " + courseExecutionList);
        return authUser.getAuthDto(JwtTokenProvider.generateToken(authUser), null, courseExecutionList);
    }

    //Was from user service
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AuthUser createDemoStudent() {
        String birthDate = LocalDateTime.now().toString() + new Random().nextDouble();
        AuthUser authUser = createUserWithAuth("Demo-Student-" + birthDate, "Demo-Student-" + birthDate, "demo_student@mail.com", Role.STUDENT, AuthUser.Type.DEMO);
        //TODO: fix this
        /*CourseExecutionDto courseExecution = authRequiredService.getDemoCourseExecution();

        if (courseExecution != null) {
            // In addCourse method of User, it already adds the user to the course execution so there is no need to do it here again
            userService.addCourseExecution(authUser.getUserSecurityInfo().getId(), courseExecution.getCourseExecutionId());
            authUser.addCourseExecution(courseExecution.getId());
        }*/
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
        return authUser.getAuthDto(JwtTokenProvider.generateToken(authUser), null, courseExecutionList);
    }

    @Retryable(
            value = { SQLException.class },
            maxAttempts = 2,
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto demoAdminAuth() {
        AuthUser authUser = getDemoAdmin();
        List<CourseExecutionDto> courseExecutionList = getCourseExecutions(authUser);
        return authUser.getAuthDto(JwtTokenProvider.generateToken(authUser), null, courseExecutionList);
    }

    //Was from user service
    @Transactional(isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED)
    public NotificationResponse<CourseExecutionDto> registerListOfUsersTransactional(InputStream stream, int courseExecutionId) {
        Notification notification = new Notification();
        extractUserDtos(stream, notification).forEach(userDto ->
                registerExternalUserTransactional(courseExecutionId, userDto)
        );

        CourseExecutionDto courseExecutionDto = authRequiredService.getCourseExecutionById(courseExecutionId);

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
                    notification.addError(String.format(ErrorMessage.WRONG_FORMAT_ON_CSV_LINE.label, lineNumber),
                            new TutorException(ErrorMessage.INVALID_CSV_FILE_FORMAT));
                    lineNumber++;
                    continue;
                }

                if (userInfo[0].length() == 0 || !userInfo[0].matches(MAIL_FORMAT) || userInfo[1].length() == 0) {
                    notification.addError(String.format(ErrorMessage.WRONG_FORMAT_ON_CSV_LINE.label, lineNumber),
                            new TutorException(ErrorMessage.INVALID_CSV_FILE_FORMAT));
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
            throw new TutorException(ErrorMessage.CANNOT_OPEN_FILE);
        }

        if (notification.hasErrors())
            return new ArrayList<>();

        return userDtos;
    }

    private List<CourseExecutionDto> getActiveTecnicoCourses(List<CourseExecutionDto> courses) {
        return courses.stream()
                /*.map(courseDto ->
                        courseExecutionService.getCourseExecutionByFields(courseDto.getAcronym(),courseDto.getAcademicTerm(), CourseType.TECNICO.name())
                )*///TODO: fix this
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private AuthUser getDemoTeacher() {
        return authUserRepository.findAuthUserByUsername(TEACHER_USERNAME).orElseGet(() -> {
            AuthUser authUser = createUserWithAuth("Demo Teacher", TEACHER_USERNAME, "demo_teacher@mail.com",  Role.TEACHER, AuthUser.Type.DEMO);
            //TODO: fix this
            //courseExecutionService.addUserToTecnicoCourseExecution(authUser.getUserSecurityInfo().getId(), courseExecutionService.getDemoCourseExecution().getId());
            return authUser;
        });
    }

    private AuthUser getDemoStudent() {
        return authUserRepository.findAuthUserByUsername(STUDENT_USERNAME).orElseGet(() -> {
            logger.info("Did not find authUser by Username");
            AuthUser authUser = createUserWithAuth("Demo Student", STUDENT_USERNAME, "demo_student@mail.com", Role.STUDENT, AuthUser.Type.DEMO);
            logger.info("Auth User created with success: " + authUser);

            Integer demoCourseExecutionId = authRequiredService.getDemoCourseExecution();
            logger.info("Demo Course Execution Id: " + demoCourseExecutionId);

            logger.info("Will create authsaga data");
            CreateUserWithAuthSagaData data = new CreateUserWithAuthSagaData(authUser.getId(), authUser.getUserSecurityInfo().getName(),
                    authUser.getUserSecurityInfo().getRole(), authUser.getUsername(), authUser.getType() != AuthUser.Type.EXTERNAL,
                    false, demoCourseExecutionId);
            SagaInstance sagaInstance = sagaInstanceFactory.create(createUserWithAuthSaga, data);
            logger.info("Data created: " + data);

            /*while (!sagaInstance.isEndState()) {
                logger.info("Waiting for saga");
            }*/
            return authUser;
        });
    }

    private AuthUser getDemoAdmin() {
        return authUserRepository.findAuthUserByUsername(ADMIN_USERNAME).orElseGet(() -> {
            AuthUser authUser = createUserWithAuth("Demo Admin", ADMIN_USERNAME, "demo_admin@mail.com", Role.DEMO_ADMIN, AuthUser.Type.DEMO);
            //TODO: fix this
            //courseExecutionService.addUserToTecnicoCourseExecution(authUser.getUserSecurityInfo().getId(), courseExecutionService.getDemoCourseExecution().getId());
            return authUser;
        });
    }

    private AuthExternalUser getOrCreateUser(ExternalUserDto externalUserDto) {
        //Avoid creation of user and authUser with invalid roles (added because of tests and will be needed for sagas)
        checkRole(externalUserDto.getRole(), externalUserDto.getActive());
        //Avoid creation of user for authUser with invalid email (added because of tests and will be needed for sagas)
        checkEmail(externalUserDto.getEmail());
        String username = externalUserDto.getUsername() != null ? externalUserDto.getUsername() : externalUserDto.getEmail();
        return (AuthExternalUser) authUserRepository.findAuthUserByUsername(username)
                .orElseGet(() -> {
                    //TODO: fix this
                    //UserDto userDto = userService.createUser(externalUserDto.getName(), externalUserDto.getRole(), username, false, false);
                    //TODO: fix this(give id to securityInfo)
                    AuthUser authUser = AuthUser.createAuthUser(new UserSecurityInfo(
                                    externalUserDto.getName(), externalUserDto.getRole(), false),
                            username, externalUserDto.getEmail(), AuthUser.Type.EXTERNAL);
                    authUserRepository.save(authUser);
                    return authUser;
                });
    }

    public void checkRole(Role role, boolean isActive) {
        if (!isActive && role != null && !(role.equals(Role.STUDENT) || role.equals(Role.TEACHER))) {
            throw new TutorException(ErrorMessage.INVALID_ROLE, role.toString());
        }
    }

    public void checkEmail(String email) {
        if (email == null || !email.matches(MAIL_FORMAT))
            throw new TutorException(ErrorMessage.INVALID_EMAIL, email);
    }

    public void approveAuthUser(Integer authUserId, Integer userId) {
        AuthUser authUser = authUserRepository.findById(authUserId).orElseThrow(() -> new TutorException(ErrorMessage.AUTHUSER_NOT_FOUND, authUserId));
        authUser.authUserApproved(userId);
    }

    public void rejectAuthUser(Integer authUserId) {
        AuthUser authUser = authUserRepository.findById(authUserId).orElseThrow(() -> new TutorException(ErrorMessage.AUTHUSER_NOT_FOUND, authUserId));
        authUser.authUserRejected();
    }


    // TODO: Uncomment when impexp is working again
    /*public String exportUsers() {
        UsersXmlExport xmlExporter = new UsersXmlExport();
        return xmlExporter.export(userRepository.findAll());
    }

    public void importUsers(String usersXML) {
        UsersXmlImport xmlImporter = new UsersXmlImport();
        xmlImporter.importUsers(usersXML, this);
    }*/

}