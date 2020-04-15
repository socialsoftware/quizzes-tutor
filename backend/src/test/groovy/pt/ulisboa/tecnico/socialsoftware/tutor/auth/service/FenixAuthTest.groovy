package pt.ulisboa.tecnico.socialsoftware.tutor.auth.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.AuthService
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.FenixEduInterface
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

import java.util.stream.Collectors

@DataJpaTest
class FenixAuthTest extends Specification {
    public static final String USERNAME = "ist12628"
    public static final String PERSON_NAME = "NAME"
    public static final String COURSE_NAME = "Arquitecturas de Software"
    public static final String ACRONYM = "ASof7"
    public static final String ACADEMIC_TERM = "1º Semestre 2019/2020"

    @Autowired
    private AuthService authService

    @Autowired
    private UserRepository userRepository

    @Autowired
    private CourseRepository courseRepository

    @Autowired
    private CourseExecutionRepository courseExecutionRepository

    def client
    def courses

    def setup() {
        client = Mock(FenixEduInterface)

        courses = new ArrayList<>()
        def courseDto = new CourseDto(COURSE_NAME, ACRONYM, ACADEMIC_TERM)
        courses.add(courseDto)
        courseDto = new CourseDto("Tópicos Avançados em Engenharia de Software", "TAES", ACADEMIC_TERM)
        courses.add(courseDto)
    }

    def "no teacher has courses create teacher"() {
        given:
        client.getPersonName() >> PERSON_NAME
        client.getPersonUsername() >> USERNAME
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> courses

        when:
        def result = authService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == USERNAME
        result.user.name == PERSON_NAME
        and: 'the user is created in the db'
        userRepository.findAll().size() == 1
        userRepository.findByUsername(USERNAME) != null
        and: 'no courses are created'
        courseRepository.findAll().size() == 0
        courseExecutionRepository.findAll().size() == 0
    }

    def "no teacher has course and is in database, then create and add"() {
        given: 'a course execution'
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseRepository.save(course)
        and:
        client.getPersonName() >> PERSON_NAME
        client.getPersonUsername() >> USERNAME
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> courses

        when:
        def result = authService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == USERNAME
        result.user.name == PERSON_NAME
        and: 'the user is created in the db'
        userRepository.findAll().size() == 1
        def user = userRepository.findByUsername(USERNAME)
        user != null
        and: 'is teaching'
        user.getCourseExecutions().size() == 1
        user.getCourseExecutions().stream().collect(Collectors.toList()).get(0).getAcronym() == ACRONYM
    }

    def "no teacher does not have courses throw exception"() {
        given:
        client.getPersonName() >> PERSON_NAME
        client.getPersonUsername() >> USERNAME
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        authService.fenixAuth(client)

        then:
        thrown(TutorException)
        and: 'the user is not created db'
        userRepository.findAll().size() == 0
        and: 'no courses are created'
        courseRepository.findAll().size() == 0
        courseExecutionRepository.findAll().size() == 0
    }

    def "teacher has courses"() {
        given: 'a teacher'
        def user = new User()
        user.setKey(1)
        user.setUsername(USERNAME)
        user.setName(PERSON_NAME)
        user.setRole(User.Role.TEACHER)
        userRepository.save(user)
        and:
        client.getPersonName() >> PERSON_NAME
        client.getPersonUsername() >> USERNAME
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> courses

        when:
        def result = authService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == USERNAME
        result.user.name == PERSON_NAME
        and: 'the user is created in the db'
        userRepository.findAll().size() == 1
        userRepository.findByUsername(USERNAME) != null
        and: 'no courses are created'
        courseRepository.findAll().size() == 0
        courseExecutionRepository.findAll().size() == 0
    }

    def "teacher does not have courses"() {
        given: 'a teacher'
        def user = new User()
        user.setKey(1)
        user.setUsername(USERNAME)
        user.setName(PERSON_NAME)
        user.setRole(User.Role.TEACHER)
        userRepository.save(user)
        and:
        client.getPersonName() >> PERSON_NAME
        client.getPersonUsername() >> USERNAME
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        def result = authService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == USERNAME
        result.user.name == PERSON_NAME
        and: 'the user is created in the db'
        userRepository.findAll().size() == 1
        userRepository.findByUsername(USERNAME) != null
        and: 'no courses are created'
        courseRepository.findAll().size() == 0
        courseExecutionRepository.findAll().size() == 0
    }

    def "teacher has course and is in database, then add"() {
        given: 'a teacher'
        def user = new User()
        user.setKey(1)
        user.setUsername(USERNAME)
        user.setName(PERSON_NAME)
        user.setRole(User.Role.TEACHER)
        userRepository.save(user)
        and: 'a course execution'
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseRepository.save(course)
        and:
        client.getPersonName() >> PERSON_NAME
        client.getPersonUsername() >> USERNAME
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> courses

        when:
        def result = authService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == USERNAME
        result.user.name == PERSON_NAME
        and: 'the user is created in the db'
        userRepository.findAll().size() == 1
        def user2 = userRepository.findByUsername(USERNAME)
        user2 != null
        and: 'is teaching'
        user2.getCourseExecutions().size() == 1
        user2.getCourseExecutions().stream().collect(Collectors.toList()).get(0).getAcronym() == ACRONYM
    }

    def "no student no courses, throw exception"() {
        given:
        client.getPersonName() >> PERSON_NAME
        client.getPersonUsername() >> USERNAME
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        authService.fenixAuth(client)

        then:
        thrown(TutorException)
        and: 'the user is not created in the db'
        userRepository.findAll().size() == 0
        and: 'no courses are created'
        courseRepository.findAll().size() == 0
        courseExecutionRepository.findAll().size() == 0
    }

    def "no student has courses but not in database, throw exception"() {
        given:
        client.getPersonName() >> PERSON_NAME
        client.getPersonUsername() >> USERNAME
        client.getPersonAttendingCourses() >> courses
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        authService.fenixAuth(client)

        then:
        thrown(TutorException)
        and: 'the user is not created in the db'
        userRepository.findAll().size() == 0
        and: 'no courses are created'
        courseRepository.findAll().size() == 0
        courseExecutionRepository.findAll().size() == 0
    }

    def "no student has courses and in database, create student with attending course"() {
        given: 'a course execution'
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseRepository.save(course)
        and:
        client.getPersonName() >> PERSON_NAME
        client.getPersonUsername() >> USERNAME
        client.getPersonAttendingCourses() >> courses
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        def result = authService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == USERNAME
        result.user.name == PERSON_NAME
        and: 'the user is created in the db'
        userRepository.findAll().size() == 1
        User user = userRepository.findByUsername(USERNAME)
        user.getRole() == User.Role.STUDENT
        and: 'is enrolled'
        user.getCourseExecutions().size() == 1
        user.getCourseExecutions().stream().collect(Collectors.toList()).get(0).getAcronym() == ACRONYM
    }

    def "student does not have courses, throw exception"() {
        given: 'a student'
        def user = new User()
        user.setKey(1)
        user.setUsername(USERNAME)
        user.setName(PERSON_NAME)
        user.setRole(User.Role.STUDENT)
        userRepository.save(user)
        and:
        client.getPersonName() >> PERSON_NAME
        client.getPersonUsername() >> USERNAME
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        authService.fenixAuth(client)

        then: "the returned data are correct"
        thrown(TutorException)
        and: 'the user is created in the db'
        userRepository.findAll().size() == 1
        and: 'is not enrolled'
        user.getCourseExecutions().size() == 0
    }

    def "student has courses but not in the database, throw exception"() {
        given: 'a student'
        def user = new User()
        user.setKey(1)
        user.setUsername(USERNAME)
        user.setName(PERSON_NAME)
        user.setRole(User.Role.STUDENT)
        userRepository.save(user)
        and:
        client.getPersonName() >> PERSON_NAME
        client.getPersonUsername() >> USERNAME
        client.getPersonAttendingCourses() >> courses
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        authService.fenixAuth(client)

        then: "the returned data are correct"
        thrown(TutorException)
        and: 'the user is created in the db'
        userRepository.findAll().size() == 1
        and: 'is not enrolled'
        user.getCourseExecutions().size() == 0
    }

    def "student has courses and in the database, add course"() {
        given: 'a student'
        def user = new User()
        user.setKey(1)
        user.setUsername(USERNAME)
        user.setName(PERSON_NAME)
        user.setRole(User.Role.STUDENT)
        userRepository.save(user)
        and: 'a course execution'
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseRepository.save(course)
        and:
        client.getPersonName() >> PERSON_NAME
        client.getPersonUsername() >> USERNAME
        client.getPersonAttendingCourses() >> courses
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        def result = authService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == USERNAME
        result.user.name == PERSON_NAME
        and: 'the user is created in the db'
        userRepository.findAll().size() == 1
        userRepository.findByUsername(USERNAME).getRole() == User.Role.STUDENT
        and: 'is enrolled'
        user.getCourseExecutions().size() == 1
        user.getCourseExecutions().stream().collect(Collectors.toList()).get(0).getAcronym() == ACRONYM
    }

    def "student has teaching courses, throw exception"() {
        given: 'a student'
        def user = new User()
        user.setKey(1)
        user.setUsername(USERNAME)
        user.setName(PERSON_NAME)
        user.setRole(User.Role.STUDENT)
        userRepository.save(user)
        and: 'a course execution'
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseRepository.save(course)
        and:
        client.getPersonName() >> PERSON_NAME
        client.getPersonUsername() >> USERNAME
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> courses

        when:
        authService.fenixAuth(client)

        then:
        thrown(TutorException)
        and: 'the user is created in the db'
        userRepository.findAll().size() == 1
        userRepository.findByUsername(USERNAME).getRole() == User.Role.STUDENT
        and: 'is not enrolled'
        user.getCourseExecutions().size() == 0
    }

    def "teacher has attending courses, does not add course"() {
        given: 'a teacher'
        def user = new User()
        user.setKey(1)
        user.setUsername(USERNAME)
        user.setName(PERSON_NAME)
        user.setRole(User.Role.TEACHER)
        userRepository.save(user)
        and: 'a course execution'
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseRepository.save(course)
        and:
        client.getPersonName() >> PERSON_NAME
        client.getPersonUsername() >> USERNAME
        client.getPersonAttendingCourses() >> courses
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        def result = authService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == USERNAME
        result.user.name == PERSON_NAME
        and: 'the user is created in the db'
        userRepository.findAll().size() == 1
        userRepository.findByUsername(USERNAME).getRole() == User.Role.TEACHER
        and: 'is enrolled'
        user.getCourseExecutions().size() == 0
    }

    def "student has attending and teaching courses, add attending course"() {
        given: 'a teacher'
        def user = new User()
        user.setKey(1)
        user.setUsername(USERNAME)
        user.setName(PERSON_NAME)
        user.setRole(User.Role.TEACHER)
        userRepository.save(user)
        and: 'a course execution'
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseRepository.save(course)
        and:
        client.getPersonName() >> PERSON_NAME
        client.getPersonUsername() >> USERNAME
        client.getPersonAttendingCourses() >> courses
        client.getPersonTeachingCourses() >> courses

        when:
        def result = authService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == USERNAME
        result.user.name == PERSON_NAME
        and: 'the user is created in the db'
        userRepository.findAll().size() == 1
        userRepository.findByUsername(USERNAME).getRole() == User.Role.TEACHER
        and: 'is enrolled'
        user.getCourseExecutions().size() == 1
        user.getCourseExecutions().stream().collect(Collectors.toList()).get(0).getAcronym() == ACRONYM
    }

    @TestConfiguration
    static class AuthServiceImplTestContextConfiguration {

        @Bean
        AuthService authService() {
            return new AuthService()
        }

        @Bean
        UserService userService() {
            return new UserService()
        }
    }
}
