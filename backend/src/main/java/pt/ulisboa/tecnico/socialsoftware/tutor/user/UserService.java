package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.AuthUserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthExternalUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.Notification;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.NotificationResponse;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.DemoAdmin;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Autowired
    public AuthUserService authUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static final String MAIL_FORMAT = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    public static final String PASSWORD_CONFIRMATION_MAIL_SUBJECT = "Password Confirmation";
    public static final String PASSWORD_CONFIRMATION_MAIL_BODY = "Link to password confirmation page";

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User createUser(String name, User.Role role) {
        User user;
        switch (role) {
            case STUDENT:
                user = new Student(name, false);
                break;
            case TEACHER:
                user = new Teacher(name, false);
                break;
            case DEMO_ADMIN:
                user = new DemoAdmin(name, false);
                break;
            default:
                throw new TutorException(USERS_IMPORT_ERROR, "Not allowed role " + role);
        }
        userRepository.save(user);
        return user;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthUser createStudentWithAuth(String name, String username, String email, AuthUser.Type type) {
        if (authUserRepository.findAuthUserByUsername(username).isPresent()) {
            throw new TutorException(DUPLICATE_USER, username);
        }

        Student student = new Student(name, username, email, false, type);
        userRepository.save(student);
        return student.getAuthUser();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthUser createTeacherWithAuth(String name, String username, String email, AuthUser.Type type) {
        if (authUserRepository.findAuthUserByUsername(username).isPresent()) {
            throw new TutorException(DUPLICATE_USER, username);
        }

        Teacher teacher = new Teacher(name, username, email, false, type);
        userRepository.save(teacher);
        return teacher.getAuthUser();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthUser createDemoAdminWithAuth(String name, String username, String email, AuthUser.Type type) {
        if (authUserRepository.findAuthUserByUsername(username).isPresent()) {
            throw new TutorException(DUPLICATE_USER, username);
        }

        DemoAdmin demoAdmin = new DemoAdmin(name, username, email, false, type);
        userRepository.save(demoAdmin);
        return demoAdmin.getAuthUser();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addCourseExecution(int userId, int executionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        user.addCourse(courseExecution);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AuthUser createDemoStudent() {
        String birthDate = LocalDateTime.now().toString() + new Random().nextDouble();
        AuthUser authUser = createStudentWithAuth("Demo-Student-" + birthDate, "Demo-Student-" + birthDate, "demo_student@mail.com", AuthUser.Type.DEMO);
        CourseExecution courseExecution = courseExecutionService.getDemoCourseExecution();
        if (courseExecution != null) {
            courseExecution.addUser(authUser.getUser());
            authUser.getUser().addCourse(courseExecution);
        }
        return authUser;
    }

    public String exportUsers() {
        UsersXmlExport xmlExporter = new UsersXmlExport();
        return xmlExporter.export(userRepository.findAll());
    }

    public void importUsers(String usersXML) {
        UsersXmlImport xmlImporter = new UsersXmlImport();
        xmlImporter.importUsers(usersXML, this);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoStudents() {
        userRepository.findAll()
                .stream()
                .filter(user -> user.getAuthUser().isDemoStudent())
                .forEach(user -> {
                    user.remove();
                    this.userRepository.delete(user);
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED)
    public NotificationResponse<CourseExecutionDto> registerListOfUsersTransactional(InputStream stream, int courseExecutionId) {
        Notification notification = new Notification();
        extractUserDtos(stream, notification).forEach(userDto ->
            registerExternalUserTransactional(courseExecutionId, userDto)
        );

        CourseExecutionDto courseExecutionDto = courseExecutionRepository.findById(courseExecutionId)
                .map(CourseExecutionDto::new)
                .get();

        return new NotificationResponse<>(notification, courseExecutionDto);
    }

    private List<ExternalUserDto> extractUserDtos(InputStream stream, Notification notification) {
        List<ExternalUserDto> userDtos = new ArrayList<>();
        String line = "";
        String cvsSplitBy = ",";
        User.Role auxRole;
        int lineNumber = 1;
        InputStreamReader isr = new InputStreamReader(stream, StandardCharsets.UTF_8);
        try (BufferedReader br = new BufferedReader(isr)) {
            while ((line = br.readLine()) != null) {
                String[] userInfo = line.split(cvsSplitBy);
                if (userInfo.length == 3) {
                    auxRole = User.Role.STUDENT;
                } else if (userInfo.length == 4 && (userInfo[3].equalsIgnoreCase("student")
                            || userInfo[3].equalsIgnoreCase("teacher"))) {
                    auxRole = User.Role.valueOf(userInfo[3].toUpperCase());
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
            throw new TutorException(ErrorMessage.CANNOT_OPEN_FILE);
        }

        if (notification.hasErrors())
            return new ArrayList<>();

        return userDtos;
    }


    @Transactional(isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED)
    public ExternalUserDto registerExternalUserTransactional(Integer courseExecutionId, ExternalUserDto externalUserDto) {
        CourseExecution courseExecution = getExternalCourseExecution(courseExecutionId);
        AuthExternalUser authUser = getOrCreateUser(externalUserDto);

        if (authUser.getUser().getCourseExecutions().contains(courseExecution)) {
            throw new TutorException(DUPLICATE_USER, authUser.getUsername());
        }

        authUser.getUser().addCourse(courseExecution);
        authUser.generateConfirmationToken();
        return new ExternalUserDto(authUser);
    }

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
        }
        catch (TutorException e) {
            if (e.getErrorMessage().equals(ErrorMessage.EXPIRED_CONFIRMATION_TOKEN)) {
                authUser.generateConfirmationToken();
            }
            else throw new TutorException(e.getErrorMessage());
        }

        return new ExternalUserDto(authUser);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDto findUserByUsername(String username) {
        return  authUserRepository.findAuthUserByUsername(username)
                    .filter(authUser -> authUser.getUser() != null)
                    .map(authUser -> new UserDto(authUser.getUser()))
                    .orElse(null);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDto findUserById(Integer id) {
        return  userRepository.findById(id)
                .filter(Objects::nonNull)
                .map(UserDto::new)
                .orElse(null);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean existsUserWithKey(Integer key) {
        return userRepository.findByKey(key).isPresent();
    }

    private AuthExternalUser getOrCreateUser(ExternalUserDto externalUserDto) {
        String username = externalUserDto.getUsername() != null ? externalUserDto.getUsername() : externalUserDto.getEmail();
        return (AuthExternalUser) authUserRepository.findAuthUserByUsername(username)
                .orElseGet(() -> {
                    User user;
                    if (externalUserDto.getRole() == User.Role.STUDENT) {
                         user = new Student(externalUserDto.getName(),
                                username, externalUserDto.getEmail(),false, AuthUser.Type.EXTERNAL);
                    } else if (externalUserDto.getRole() == User.Role.TEACHER) {
                        user = new Teacher(externalUserDto.getName(),
                                username, externalUserDto.getEmail(),false, AuthUser.Type.EXTERNAL);
                    } else {
                        throw new TutorException(INVALID_ROLE, externalUserDto.getRole() == null ? "null" : externalUserDto.getRole().name());
                    }

                    userRepository.save(user);
                    return user.getAuthUser();
                });
    }

    private CourseExecution getExternalCourseExecution(Integer courseExecutionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        if (!courseExecution.getType().equals(Course.Type.EXTERNAL)) {
            throw new TutorException(COURSE_EXECUTION_NOT_EXTERNAL, courseExecutionId);
        }
        return courseExecution;
    }

}
