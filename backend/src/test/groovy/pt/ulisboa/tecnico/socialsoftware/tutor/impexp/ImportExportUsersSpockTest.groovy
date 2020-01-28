package pt.ulisboa.tecnico.socialsoftware.tutor.impexp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

@DataJpaTest
class ImportExportUsersSpockTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String RITO = 'Rito'
    public static final String PEDRO = 'Pedro'
    public static final String AR = 'ar'
    public static final String PC = 'pc'
    public static final String TEACHER = 'TEACHER'
    public static final String STUDENT = 'STUDENT'

    @Autowired
    UserService userService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def course
    def courseExecution

    def setup() {
        course = new Course(COURSE_NAME)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM)
        courseExecutionRepository.save(courseExecution)

        def user = new User(RITO, AR, 1,2019, User.Role.TEACHER)
        user.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user)
        userRepository.save(user)

        user = new User(PEDRO, PC, 2,2019, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user)
        userRepository.save(user)
    }

    def 'export and import users'() {
        given: 'a xml with of users'
        def usersXml = userService.exportUsers()
        and: 'a clean database'
        userRepository.deleteAll()
        courseExecution.getUsers().clear()

        when:
        userService.importUsers(usersXml)

        then:
        courseExecution.getUsers().size() == 2

        userRepository.findAll().size() == 2
        def userOne = userRepository.findByUsername(AR)
        userOne != null
        userOne.getNumber() == 1
        userOne.getName() == RITO
        userOne.getRole().name() == TEACHER
        userOne.getCourseExecutions().size() == 1

        def userTwo = userRepository.findByUsername(PC)
        userTwo != null
        userTwo.getNumber() == 2
        userTwo.getName() == PEDRO
        userTwo.getRole().name() == STUDENT
        userOne.getCourseExecutions().size() == 1
    }

    @TestConfiguration
    static class UserServiceImplTestContextConfiguration {

        @Bean
        UserService userService() {
            return new UserService()
        }
    }

}
