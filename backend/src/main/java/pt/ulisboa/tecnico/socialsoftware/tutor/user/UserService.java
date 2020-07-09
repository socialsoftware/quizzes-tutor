package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.Demo;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public User createUser(String name, String username, User.Role role) {
        if (findByUsername(username) != null) {
            throw new TutorException(DUPLICATE_USER, username);
        }

        User user = new User(name, username, role);
        userRepository.save(user);
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
            User user = createUser("Demo Teacher", Demo.TEACHER_USERNAME, User.Role.TEACHER);
            user.addCourse(courseService.getDemoCourseExecution());
            return user;
        });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User getDemoStudent() {
        return this.userRepository.findByUsername(Demo.STUDENT_USERNAME).orElseGet(() -> {
            User user = createUser("Demo Student", Demo.STUDENT_USERNAME, User.Role.STUDENT);
            user.addCourse(courseService.getDemoCourseExecution());
            return user;
        });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User getDemoAdmin() {
        return this.userRepository.findByUsername(Demo.ADMIN_USERNAME).orElseGet(() -> {
            User user = createUser("Demo Admin", Demo.ADMIN_USERNAME, User.Role.DEMO_ADMIN);
            user.addCourse(courseService.getDemoCourseExecution());
            return user;
        });
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User createDemoStudent() {
        String birthDate = LocalDateTime.now().toString() + new Random().nextDouble();
        User newDemoUser = createUser("Demo-Student-" + birthDate, "Demo-Student-" + birthDate, User.Role.STUDENT);
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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ExternalUserDto createExternalUser(Integer courseExecutionId, ExternalUserDto externalUserDto) {

        if(externalUserDto.getEmail() == null || externalUserDto.getEmail().trim().equals(""))
            throw new TutorException(INVALID_EMAIL, externalUserDto.getEmail());

        if(externalUserDto.getPassword() == null || externalUserDto.getPassword().trim().equals(""))
            throw new TutorException(INVALID_PASSWORD, externalUserDto.getPassword());

        if(externalUserDto.getRole() == null)
            throw new TutorException(INVALID_ROLE);

        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        if (courseExecution.getType() != Course.Type.EXTERNAL)
            throw new TutorException(COURSE_EXECUTION_NOT_EXTERNAL, courseExecutionId);

        Optional<User> user = userRepository.findByUsername(externalUserDto.getEmail());
        User user1;
        if(user.isPresent()){
            user.get().addCourse(courseExecution);
            user1 = user.get();
        }else{
            user1 = new User("", externalUserDto.getEmail(), externalUserDto.getRole());
            userRepository.save(user1);
        }

        user1.setEmail(externalUserDto.getEmail());
        user1.setPassword(externalUserDto.getPassword());

        courseExecution.addUser(user1);
        user1.addCourse(courseExecution);
        user1.setAdmin(false);
        return new ExternalUserDto(user1);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean confirmRegistration(ExternalUserDto externalUserDto) {
        User user = findByUsername(externalUserDto.getEmail());

        return false;
    }

}
