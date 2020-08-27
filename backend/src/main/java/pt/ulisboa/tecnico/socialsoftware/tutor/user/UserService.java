package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.AuthUserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.dto.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.Notification;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.NotificationResponse;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.mailer.Mailer;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.AuthUserRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
    private CourseService courseService;

    @Autowired
    private AnswerService answerService;

     @Autowired
     public AuthUserService authUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Mailer mailer;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public static final String MAIL_FORMAT = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    public static final String PASSWORD_CONFIRMATION_MAIL_SUBJECT = "Quiz-Tutor Password Confirmation";
    public static final String PASSWORD_CONFIRMATION_MAIL_BODY = "Link to password confirmation page";

    public User findByKey(Integer key) {
        return this.userRepository.findByKey(key).orElse(null);
    }

    public Integer getMaxUserNumber() {
        Integer result = userRepository.getMaxUserNumber();
        return result != null ? result : 0;
    }

    public User createUser(String name, User.Role role) {
        User user = new User(name, role, false);
        userRepository.save(user);
        user.setKey(user.getId());
        return user;
    }

    public AuthUser createUserWithAuth(String name, String username, String email, User.Role role, AuthUser.Type type) {
        if (authUserService.findAuthUserByUsername(username) != null) {
            throw new TutorException(DUPLICATE_USER, username);
        }

        User user = new User(name, username, email, role, false, type);
        userRepository.save(user);
        user.setKey(user.getId());
        return user.getAuthUser();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String getEnrolledCoursesAcronyms(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        AuthUser authUser = user.getAuthUser();
        return authUser.getEnrolledCoursesAcronyms();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean userHasAnExecutionOfCourse(int userId, int courseId) {
        return userRepository.findUserWithCourseExecutionsById(userId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId))
                .getCourseExecutions().stream()
                .anyMatch(courseExecution -> courseExecution.getCourse().getId().equals(courseId));
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
        AuthUser authUser = createUserWithAuth("Demo-Student-" + birthDate, "Demo-Student-" + birthDate, "demo_student@mail.com", User.Role.STUDENT, AuthUser.Type.EXTERNAL);
        CourseExecution courseExecution = courseService.getDemoCourseExecution();
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
                .filter(user -> user.getName().startsWith("Demo-Student-"))
                .forEach(user -> {
                    for (QuizAnswer quizAnswer : new ArrayList<>(user.getQuizAnswers())) {
                        answerService.deleteQuizAnswer(quizAnswer);
                    }

                    this.userRepository.delete(user);
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED)
    public NotificationResponse<CourseDto> importListOfUsersTransactional(InputStream stream, int courseExecutionId) {
        Notification notification = new Notification();
        extractUserDtos(stream, notification).forEach(userDto -> createExternalUserTransactional(courseExecutionId, userDto));

        CourseDto courseDto = courseExecutionRepository.findById(courseExecutionId)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        return new NotificationResponse<>(notification, courseDto);
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
                if (userInfo.length == 2) {
                    auxRole = User.Role.STUDENT;
                } else if (userInfo.length == 3 && (userInfo[2].equalsIgnoreCase("student") 
                            || userInfo[2].equalsIgnoreCase("teacher"))) {
                    auxRole = User.Role.valueOf(userInfo[2].toUpperCase());
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
                userDto.setName(userInfo[1]);
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
    public ExternalUserDto createExternalUserTransactional(Integer courseExecutionId, ExternalUserDto externalUserDto) {
        CourseExecution courseExecution = getExternalCourseExecution(courseExecutionId);
        User user = getOrCreateUser(externalUserDto).getUser();
        associateUserWithExecution(courseExecution, user);
        generateConfirmationToken(user.getAuthUser());
        return new ExternalUserDto(user.getAuthUser());
    }

    public String generateConfirmationToken(AuthUser authUser) {
        String token = KeyGenerators.string().generateKey();
        authUser.setTokenGenerationDate(LocalDateTime.now());
        authUser.setConfirmationToken(token);
        return token;
    }

    private void associateUserWithExecution(CourseExecution courseExecution, User user) {
        if(courseExecution.getUsers().stream().map(User::getId).collect(Collectors.toList()).contains(user.getId()))
            throw new TutorException(DUPLICATE_USER, user.getUsername());

        courseExecution.addUser(user);
        user.addCourse(courseExecution);
    }

    private AuthUser getOrCreateUser(ExternalUserDto externalUserDto) {
        return authUserRepository.findAuthUserByUsername(externalUserDto.getEmail())
                .orElseGet(() -> {
                    User user = new User(externalUserDto.getName(),
                            externalUserDto.getEmail(), externalUserDto.getEmail(),
                            externalUserDto.getRole(), false, AuthUser.Type.EXTERNAL);
                    userRepository.save(user);
                    user.setKey(user.getId());
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
