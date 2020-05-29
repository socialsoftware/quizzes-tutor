package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.Demo;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlImport;

import java.time.LocalDateTime;
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
    public List<CourseDto> getCourseExecutions(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        return user.getCourseExecutions().stream().map(CourseDto::new).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addCourseExecution(int userId, int executionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        user.addCourse(courseExecution);
    }

    public User getDemoTeacher() {
        return this.userRepository.findByUsername(Demo.TEACHER_USERNAME).orElseGet(() -> {
            User user = createUser("Demo Teacher", Demo.TEACHER_USERNAME, User.Role.TEACHER);
            user.addCourse(courseService.getDemoCourseExecution());
            return user;
        });
    }

    public User getDemoStudent() {
        return this.userRepository.findByUsername(Demo.STUDENT_USERNAME).orElseGet(() -> {
            User user = createUser("Demo Student", Demo.STUDENT_USERNAME, User.Role.STUDENT);
            user.addCourse(courseService.getDemoCourseExecution());
            return user;
        });
    }

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
}
