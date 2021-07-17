package pt.ulisboa.tecnico.socialsoftware.auth.services.local;

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
import pt.ulisboa.tecnico.socialsoftware.auth.domain.*;
import pt.ulisboa.tecnico.socialsoftware.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.confirmRegistration.ConfirmRegistrationSaga;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.confirmRegistration.ConfirmRegistrationSagaData;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.createUserWithAuth.CreateUserWithAuthSaga;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.createUserWithAuth.CreateUserWithAuthSagaData;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.updateCourseExecutions.UpdateCourseExecutionsSaga;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.updateCourseExecutions.UpdateCourseExecutionsSagaData;
import pt.ulisboa.tecnico.socialsoftware.auth.services.remote.AuthUserRequiredService;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.auth.AuthDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.auth.AuthUserType;
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

import static pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUserState.UPDATE_PENDING;
import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.*;
import static pt.ulisboa.tecnico.socialsoftware.common.utils.Utils.*;

@Service
public class AuthUserProvidedService {

    private static final Logger logger = LoggerFactory.getLogger(AuthUserProvidedService.class);

    @Autowired
    private AuthUserRequiredService authRequiredService;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SagaInstanceFactory sagaInstanceFactory;

    @Autowired
    private CreateUserWithAuthSaga createUserWithAuthSaga;

    @Autowired
    private UpdateCourseExecutionsSaga updateCourseExecutionsSaga;

    @Autowired
    private ConfirmRegistrationSaga confirmRegistrationSaga;

    public AuthDto fenixAuth(FenixEduInterface fenix) {
        String username = fenix.getPersonUsername();
        AuthTecnicoUser authUser;
        try {
            authUser = (AuthTecnicoUser) authUserRepository.findAuthUserByUsername(username).orElse(null);
        } catch (ClassCastException e) {
            throw new TutorException(ErrorMessage.INVALID_AUTH_USERNAME, username);
        }

        if (authUser == null) {
            authUser = createAuthTecnicoUser(fenix, username);
        } else {
            refreshFenixAuthUserInfo(fenix, authUser);
        }

        List<CourseExecutionDto> courseExecutionList = getCourseExecutions(authUser);

        if (authUser.getUserSecurityInfo().isTeacher()) {
            return authUser.getAuthDto(JwtTokenProvider.generateToken(authUser), fenix.getPersonTeachingCourses(), courseExecutionList);
        } else {
            return authUser.getAuthDto(JwtTokenProvider.generateToken(authUser), null, courseExecutionList);
        }
    }

    private List<CourseExecutionDto> getCourseExecutions(AuthUser authUser) {
        Integer userId = authUser.getUserSecurityInfo().getId();
        return authRequiredService.getUserCourseExecutions(userId).getCourseExecutionDtoList();
    }

    private void refreshFenixAuthUserInfo(FenixEduInterface fenix, AuthTecnicoUser authUser) {
        if (authUser.getUserSecurityInfo().isTeacher()) {
            List<CourseExecutionDto> fenixTeachingCourses = fenix.getPersonTeachingCourses();
            updateTeacherCourses(authUser, fenixTeachingCourses, fenix.getPersonEmail());
        } else {
            List<CourseExecutionDto> fenixAttendingCourses = fenix.getPersonAttendingCourses();
            updateStudentCourses(authUser, fenixAttendingCourses, fenix.getPersonEmail());
        }
    }

    private void updateTeacherCourses(AuthTecnicoUser authUser, List<CourseExecutionDto> fenixTeachingCourses, String email) {
        List<CourseExecutionDto> activeTeachingCourses = getActiveTecnicoCourses(fenixTeachingCourses);
        if (!fenixTeachingCourses.isEmpty() && authUser.getUserSecurityInfo().isTeacher()) {

            List<CourseExecutionDto> courseExecutionDtoList = activeTeachingCourses.stream()
                    .filter(courseExecutionDto ->
                            !authUser.getUserCourseExecutions().contains(courseExecutionDto.getCourseExecutionId()))
                    .collect(Collectors.toList());

            String ids = fenixTeachingCourses.stream()
                    .map(courseDto -> courseDto.getAcronym() + courseDto.getAcademicTerm())
                    .collect(Collectors.joining(","));

            updateAuthUserCourseExecutions(authUser, courseExecutionDtoList, ids, email);
        }
    }

    private void updateStudentCourses(AuthTecnicoUser authUser, List<CourseExecutionDto> fenixAttendingCourses, String email) {
        List<CourseExecutionDto> activeAttendingCourses = getActiveTecnicoCourses(fenixAttendingCourses);
        if (!activeAttendingCourses.isEmpty() && authUser.getUserSecurityInfo().isStudent()) {

            List<CourseExecutionDto> courseExecutionDtoList = activeAttendingCourses.stream()
                    .filter(courseExecutionDto ->
                            !authUser.getUserCourseExecutions().contains(courseExecutionDto.getCourseExecutionId()))
                    .collect(Collectors.toList());

            updateAuthUserCourseExecutions(authUser, courseExecutionDtoList, null, email);
        }
    }

    private void updateAuthUserCourseExecutions(AuthUser authUser, List<CourseExecutionDto> courseExecutionDtoList,
                                                String ids, String email) {

        updateCourseExecutionsSaga(authUser, authUser.getUserSecurityInfo().getId(), ids, courseExecutionDtoList, email);

        // Waits for saga to finish
        AuthUser authUserFinal = authUserRepository.findById(authUser.getId()).get();
        while (!(authUserFinal.getState().equals(AuthUserState.APPROVED))) {

            authUserFinal = authUserRepository.findById(authUser.getId()).get();
        }
    }

    @Transactional
    public void updateCourseExecutionsSaga(AuthUser authUser, Integer userId, String ids,
                                           List<CourseExecutionDto> courseExecutionDtoList, String email) {
        authUser.setState(UPDATE_PENDING);
        authUserRepository.save(authUser);

        UpdateCourseExecutionsSagaData data = new UpdateCourseExecutionsSagaData(authUser.getId(), userId, ids,
                courseExecutionDtoList, email);
        sagaInstanceFactory.create(updateCourseExecutionsSaga, data);
    }

    private AuthTecnicoUser createAuthTecnicoUser(FenixEduInterface fenix, String username) {
        List<CourseExecutionDto> fenixAttendingCourses = fenix.getPersonAttendingCourses();
        List<CourseExecutionDto> fenixTeachingCourses = fenix.getPersonTeachingCourses();

        List<CourseExecutionDto> activeAttendingCourses = getActiveTecnicoCourses(fenixAttendingCourses);

        AuthTecnicoUser authUser = null;

        // If user is student and is not in db
        if (!activeAttendingCourses.isEmpty()) {
            List<CourseExecutionDto> activeStudentCourses = getActiveTecnicoCourses(fenixAttendingCourses);
            authUser = (AuthTecnicoUser) createAuthUser(fenix.getPersonName(), username, fenix.getPersonEmail(),
                    Role.STUDENT, AuthUserType.TECNICO, activeStudentCourses, null);
        }

        // If user is teacher and is not in db
        if (!fenixTeachingCourses.isEmpty()) {
            List<CourseExecutionDto> activeTeachingCourses = getActiveTecnicoCourses(fenixTeachingCourses);
            String ids = fenixTeachingCourses.stream()
                    .map(courseDto -> courseDto.getAcronym() + courseDto.getAcademicTerm())
                    .collect(Collectors.joining(","));
            authUser = (AuthTecnicoUser) createAuthUser(fenix.getPersonName(), username, fenix.getPersonEmail(),
                    Role.TEACHER, AuthUserType.TECNICO, activeTeachingCourses, ids);
        }

        if (authUser == null) {
            throw new TutorException(ErrorMessage.USER_NOT_ENROLLED, username);
        }
        return authUser;
    }

    //Was from user service
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthUser createUserWithAuth(String name, String username, String email, Role role, AuthUserType type, String ids) {
        if (authUserRepository.findAuthUserByUsername(username).isPresent()) {
            throw new TutorException(ErrorMessage.DUPLICATE_USER, username);
        }

        AuthUser authUser = AuthUser.createAuthUser(new UserSecurityInfo(name, role, false), username, email, type);
        if (ids != null & type.equals(AuthUserType.TECNICO) & role.equals(Role.TEACHER)) {
            // Used for Tecnico Teachers
            ((AuthTecnicoUser)authUser).setEnrolledCoursesAcronyms(ids);
        }
        authUserRepository.save(authUser);
        return authUser;
    }

    public ExternalUserDto registerExternalUserTransactional(Integer courseExecutionId, ExternalUserDto externalUserDto) {
        CourseExecutionDto courseExecutionDto = authRequiredService.getCourseExecutionById(courseExecutionId);

        if (!courseExecutionDto.getCourseExecutionType().equals(CourseType.EXTERNAL)) {
            throw new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_EXTERNAL, courseExecutionId);
        }

        checkRole(externalUserDto.getRole(), externalUserDto.getActive());
        checkEmail(externalUserDto.getEmail());
        String username = externalUserDto.getUsername() != null ? externalUserDto.getUsername() : externalUserDto.getEmail();

        List<CourseExecutionDto> courseExecutionDtoList = new ArrayList<>();
        courseExecutionDtoList.add(courseExecutionDto);

        AuthExternalUser authUser = (AuthExternalUser) authUserRepository.findAuthUserByUsername(username).orElse(null);

        if (authUser == null) {
            authUser = (AuthExternalUser) createAuthUser(externalUserDto.getName(), username, externalUserDto.getEmail(),
                    externalUserDto.getRole(), AuthUserType.EXTERNAL, courseExecutionDtoList, null);
            return authUser.getDto();
        }

        if (authUser.getUserCourseExecutions().contains(courseExecutionId)) {
            throw new TutorException(ErrorMessage.DUPLICATE_USER, username);
        }

        return authUser.getDto();
    }

    //Was from user service
    public ExternalUserDto confirmRegistrationTransactional(ExternalUserDto externalUserDto) {
        AuthExternalUser authUser = (AuthExternalUser) authUserRepository.
                findAuthUserByUsername(externalUserDto.getUsername()).orElse(null);

        if (authUser == null) {
            throw new TutorException(ErrorMessage.EXTERNAL_USER_NOT_FOUND, externalUserDto.getUsername());
        }

        if (externalUserDto.getPassword() == null || externalUserDto.getPassword().isEmpty()) {
            throw new TutorException(ErrorMessage.INVALID_PASSWORD);
        }

        try {
            checkConfirmationToken(authUser, externalUserDto.getConfirmationToken());

            authUser = confirmRegistration(authUser, passwordEncoder.encode(externalUserDto.getPassword()));
        }
        catch (TutorException e) {
            if (e.getErrorMessage().equals(ErrorMessage.EXPIRED_CONFIRMATION_TOKEN)) {
                authUser.generateConfirmationToken();
            }
            else throw new TutorException(e.getErrorMessage());
        }

        return authUser.getDto();
    }

    public AuthExternalUser confirmRegistration(AuthUser authUser, String password) {

        confirmRegistrationSaga(authUser, authUser.getId(), authUser.getUserSecurityInfo().getId(), password);

        // Waits for saga to finish
        AuthUser authUserFinal = authUserRepository.findById(authUser.getId()).get();
        while (!(authUserFinal.getState().equals(AuthUserState.APPROVED))) {
            authUserFinal = authUserRepository.findById(authUser.getId()).get();
        }
        return (AuthExternalUser) authUserFinal;
    }

    @Transactional
    public void confirmRegistrationSaga(AuthUser authUser, Integer authUserId, Integer userId, String password) {
        authUser.setState(UPDATE_PENDING);
        authUserRepository.save(authUser);

        ConfirmRegistrationSagaData data = new ConfirmRegistrationSagaData(authUserId, userId, password);
        sagaInstanceFactory.create(confirmRegistrationSaga, data);
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

    public AuthDto demoStudentAuth(Boolean createNew) {
        AuthUser authUser;

        if (createNew == null || !createNew) {
            authUser = getDemoStudent();
        }
        else {
            authUser = createDemoStudent();
        }

        List<CourseExecutionDto> courseExecutionList = getCourseExecutions(authUser);
        return authUser.getAuthDto(JwtTokenProvider.generateToken(authUser), null, courseExecutionList);
    }

    public AuthUser createDemoStudent() {
        String birthDate = LocalDateTime.now().toString() + new Random().nextDouble();
        return createAuthUser("Demo-Student-" + birthDate, "Demo-Student-" + birthDate,
                "demo_student@mail.com", Role.STUDENT, AuthUserType.DEMO, null, null);
    }

    public AuthDto demoTeacherAuth() {
        AuthUser authUser = getDemoTeacher();
        List<CourseExecutionDto> courseExecutionList = getCourseExecutions(authUser);
        return authUser.getAuthDto(JwtTokenProvider.generateToken(authUser), null, courseExecutionList);
    }

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

    @Transactional
    public List<CourseExecutionDto> getActiveTecnicoCourses(List<CourseExecutionDto> courses) {
        return courses.stream()
                .map(courseDto ->
                        authRequiredService.getCourseExecutionByFields(courseDto.getAcronym(),
                                courseDto.getAcademicTerm(), CourseType.TECNICO.name())
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private AuthUser createAuthUser(String name, String username, String email, Role role, AuthUserType type,
                                    List<CourseExecutionDto> courseExecutionDtoList, String ids) {
        AuthUser authUser = createAuthUserSaga(name, username, email, role, type, courseExecutionDtoList, ids);

        // Waits for saga to finish
        AuthUser authUserFinal = authUserRepository.findById(authUser.getId()).get();
        while (!(authUserFinal.getState().equals(AuthUserState.APPROVED) ||
                authUserFinal.getState().equals(AuthUserState.REJECTED))) {

            authUserFinal = authUserRepository.findById(authUser.getId()).get();
        }

        return authUserFinal;
    }

    @Transactional
    public AuthUser createAuthUserSaga(String name, String username, String email, Role role, AuthUserType type,
                                       List<CourseExecutionDto> courseExecutionDtoList, String ids) {
        List<CourseExecutionDto> courseExecutionDtos = courseExecutionDtoList;
        if (courseExecutionDtos == null) {
            CourseExecutionDto demoCourseExecution = authRequiredService.getDemoCourseExecution();
            courseExecutionDtos = new ArrayList<>();
            courseExecutionDtos.add(demoCourseExecution);
        }

        AuthUser authUser = createUserWithAuth(name, username, email, role, type, ids);

        CreateUserWithAuthSagaData data = new CreateUserWithAuthSagaData(authUser.getId(), authUser.getUserSecurityInfo().getName(),
                authUser.getUserSecurityInfo().getRole(), authUser.getUsername(), authUser.getType() != AuthUserType.EXTERNAL,
                courseExecutionDtos);
        sagaInstanceFactory.create(createUserWithAuthSaga, data);
        return authUser;
    }

    private AuthUser getDemoTeacher() {
        return authUserRepository.findAuthUserByUsername(TEACHER_USERNAME).orElseGet(() -> {
            return createAuthUser("Demo Teacher", TEACHER_USERNAME, "demo_teacher@mail.com",  Role.TEACHER,
                    AuthUserType.DEMO, null, null);
        });
    }

    private AuthUser getDemoStudent() {
        return authUserRepository.findAuthUserByUsername(STUDENT_USERNAME).orElseGet(() -> {
            return createAuthUser("Demo Student", STUDENT_USERNAME, "demo_student@mail.com", Role.STUDENT,
                    AuthUserType.DEMO, null, null);
        });
    }

    private AuthUser getDemoAdmin() {
        return authUserRepository.findAuthUserByUsername(ADMIN_USERNAME).orElseGet(() -> {
            return createAuthUser("Demo Admin", ADMIN_USERNAME, "demo_admin@mail.com", Role.DEMO_ADMIN,
                    AuthUserType.DEMO, null, null);
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

    public void checkConfirmationToken(AuthExternalUser authUser, String token) {
        if (authUser.isActive()) {
            throw new TutorException(USER_ALREADY_ACTIVE, authUser.getUsername());
        }
        if (!token.equals(authUser.getConfirmationToken()))
            throw new TutorException(INVALID_CONFIRMATION_TOKEN);
        if (authUser.getTokenGenerationDate().isBefore(LocalDateTime.now().minusDays(1)))
            throw new TutorException(EXPIRED_CONFIRMATION_TOKEN);
    }

    public void approveAuthUser(Integer authUserId, Integer userId, List<CourseExecutionDto> courseExecutionList) {
        AuthUser authUser = authUserRepository.findById(authUserId).orElseThrow(() -> new TutorException(ErrorMessage.AUTHUSER_NOT_FOUND, authUserId));

        switch (authUser.getType()) {
            case TECNICO:
            case DEMO:
                authUser.authUserApproved(userId, courseExecutionList);
                break;
            case EXTERNAL:
                ((AuthExternalUser)authUser).authUserApproved(userId, courseExecutionList);
                break;
            default:
                throw new TutorException(ErrorMessage.WRONG_AUTH_USER_TYPE, authUser.getType().toString());
        }
    }

    public void rejectAuthUser(Integer authUserId) {
        AuthUser authUser = authUserRepository.findById(authUserId).orElseThrow(() -> new TutorException(ErrorMessage.AUTHUSER_NOT_FOUND, authUserId));
        authUser.authUserRejected();
    }

    public void undoUpdateCourseExecutions(Integer authUserId) {
        AuthTecnicoUser authUser = (AuthTecnicoUser) authUserRepository.findById(authUserId).orElseThrow(() -> new TutorException(ErrorMessage.AUTHUSER_NOT_FOUND, authUserId));
        authUser.authUserUndoUpdateCourseExecutions();
    }

    public void confirmUpdateCourseExecutions(Integer authUserId, String ids, List<CourseExecutionDto> courseExecutionDtoList, String email) {
        AuthUser authUser = authUserRepository.findById(authUserId).orElseThrow(() -> new TutorException(ErrorMessage.AUTHUSER_NOT_FOUND, authUserId));
        switch (authUser.getType()) {
            case TECNICO:
                ((AuthTecnicoUser)authUser).authUserConfirmUpdateCourseExecutions(ids, courseExecutionDtoList, email);
                break;
            default:
                throw new TutorException(ErrorMessage.WRONG_AUTH_USER_TYPE, authUser.getType().toString());
        }
    }

    public void undoConfirmAuthUserRegistration(Integer authUserId) {
        AuthExternalUser authUser = (AuthExternalUser) authUserRepository.findById(authUserId)
                .orElseThrow(() -> new TutorException(ErrorMessage.AUTHUSER_NOT_FOUND, authUserId));
        authUser.authUserUndoConfirmRegistration();
    }

    public void confirmAuthUserRegistration(Integer authUserId, String password) {
        AuthExternalUser authUser = (AuthExternalUser) authUserRepository.findById(authUserId)
                .orElseThrow(() -> new TutorException(ErrorMessage.AUTHUSER_NOT_FOUND, authUserId));
        authUser.authUserConfirmRegistration(password);
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