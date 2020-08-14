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
import pt.ulisboa.tecnico.socialsoftware.tutor.config.Demo;
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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto;

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
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Mailer mailer;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public User findByUsername(String username) {
        return this.userRepository.findByUsername(username).orElse(null);
    }

    public User findByKey(Integer key) {
        return this.userRepository.findByKey(key).orElse(null);
    }

    public Integer getMaxUserNumber() {
        Integer result = userRepository.getMaxUserNumber();
        return result != null ? result : 0;
    }

    public User createUser(String name, String username, String email, User.Role role) {
        if (findByUsername(username) != null) {
            throw new TutorException(DUPLICATE_USER, username);
        }

        User user = new User(name, username, email, role, User.State.ACTIVE, false);
        userRepository.save(user);
        user.setState(User.State.ACTIVE);
        user.setKey(user.getId());
        return user;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String getEnrolledCoursesAcronyms(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        return user.getEnrolledCoursesAcronyms();
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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User getDemoTeacher() {
        return this.userRepository.findByUsername(Demo.TEACHER_USERNAME).orElseGet(() -> {
            User user = createUser("Demo Teacher", Demo.TEACHER_USERNAME, "demo_teacher@mail.com",  User.Role.TEACHER);
            user.addCourse(courseService.getDemoCourseExecution());
            return user;
        });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User getDemoStudent() {
        return this.userRepository.findByUsername(Demo.STUDENT_USERNAME).orElseGet(() -> {
            User user = createUser("Demo Student", Demo.STUDENT_USERNAME, "demo_student@mail.com", User.Role.STUDENT);
            user.addCourse(courseService.getDemoCourseExecution());
            return user;
        });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User getDemoAdmin() {
        return this.userRepository.findByUsername(Demo.ADMIN_USERNAME).orElseGet(() -> {
            User user = createUser("Demo Admin", Demo.ADMIN_USERNAME, "demo_admin@mail.com", User.Role.DEMO_ADMIN);
            user.addCourse(courseService.getDemoCourseExecution());
            return user;
        });
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User createDemoStudent() {
        String birthDate = LocalDateTime.now().toString() + new Random().nextDouble();
        User newDemoUser = createUser("Demo-Student-" + birthDate, "Demo-Student-" + birthDate, "demo_student@mail.com", User.Role.STUDENT);
        CourseExecution courseExecution = courseService.getDemoCourseExecution();
        if (courseExecution != null) {
            courseExecution.addUser(newDemoUser);
            newDemoUser.addCourse(courseExecution);
        }
        return newDemoUser;
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

                if (userInfo[0].length() == 0 || !userInfo[0].matches(User.MAIL_FORMAT) || userInfo[1].length() == 0) {
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
        checkRole(externalUserDto);
        User user = getOrCreateUser(externalUserDto);
        associateUserWithExecution(courseExecution, user);
        generateConfirmationToken(user);
        return new ExternalUserDto(user);
    }


    private void checkRole(ExternalUserDto externalUserDto) {
        if (externalUserDto.getRole() == null)
            throw new TutorException(INVALID_ROLE);

        if (!(externalUserDto.getRole().equals(User.Role.STUDENT) || externalUserDto.getRole().equals(User.Role.TEACHER)))
            throw new TutorException(INVALID_ROLE, externalUserDto.getRole().toString());
    }

    public String generateConfirmationToken(User user) {
        String token = KeyGenerators.string().generateKey();
        user.setTokenGenerationDate(LocalDateTime.now());
        user.setConfirmationToken(token);
        return token;
    }

    private void associateUserWithExecution(CourseExecution courseExecution, User user) {
        if(courseExecution.getUsers().stream().map(User::getId).collect(Collectors.toList()).contains(user.getId()))
            throw new TutorException(DUPLICATE_USER, user.getUsername());

        courseExecution.addUser(user);
        user.addCourse(courseExecution);
    }

    private User getOrCreateUser(ExternalUserDto externalUserDto) {
        return userRepository.findByUsername(externalUserDto.getEmail())
                .orElseGet(() -> {
                    User createdUser = new User(externalUserDto.getName(), externalUserDto.getEmail(), externalUserDto.getEmail(), externalUserDto.getRole(), User.State.INACTIVE, false);
                    userRepository.save(createdUser);
                    return createdUser;
                });
    }

    private CourseExecution getExternalCourseExecution(Integer courseExecutionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        if (courseExecution.getType() != Course.Type.EXTERNAL){
            throw new TutorException(COURSE_EXECUTION_NOT_EXTERNAL, courseExecutionId);
        }
        return courseExecution;
    }

}
