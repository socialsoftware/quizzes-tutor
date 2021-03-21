package pt.ulisboa.tecnico.socialsoftware.tutor.auth.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.FenixEduInterface
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser

import java.util.stream.Collectors

@DataJpaTest
class FenixAuthTest extends SpockTest {
    def client
    def courses
    def existingUsers
    def existingCourses
    def existingCourseExecutions

    def setup() {
        createExternalCourseAndExecution()

        client = Mock(FenixEduInterface)

        courses = new ArrayList<>()
        def courseDto = new CourseExecutionDto(COURSE_1_NAME, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM)
        courseDto.setEndDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        courses.add(courseDto)
        courseDto = new CourseExecutionDto("Tópicos Avançados em Engenharia de Software", "TAES", COURSE_1_ACADEMIC_TERM)
        courseDto.setEndDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        courses.add(courseDto)

        existingUsers = userRepository.findAll().size()
        existingCourses = courseRepository.findAll().size()
        existingCourseExecutions = courseExecutionRepository.findAll().size()
    }

    def "no teacher has courses create teacher"() {
        given:
        client.getPersonName() >> USER_1_NAME
        client.getPersonUsername() >> USER_1_USERNAME
        client.getPersonEmail() >> USER_1_EMAIL
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> courses

        when:
        def result = authUserService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == USER_1_USERNAME
        result.user.name == USER_1_NAME
        result.user.email == USER_1_EMAIL
        and: 'the user is created in the db'
        userRepository.findAll().size() == existingUsers + 1
        authUserRepository.findAuthUserByUsername(USER_1_USERNAME).orElse(null) != null
        and: 'no courses are created'
        courseRepository.findAll().size() == existingCourses
        courseExecutionRepository.findAll().size() == existingCourseExecutions
    }

    def "no teacher has course and is in database, then create and add"() {
        client.getPersonName() >> USER_1_NAME
        client.getPersonUsername() >> USER_1_USERNAME
        client.getPersonEmail() >> USER_1_EMAIL
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> courses

        when:
        def result = authUserService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == USER_1_USERNAME
        result.user.name == USER_1_NAME
        result.user.email == USER_1_EMAIL
        and: 'the user is created in the db'
        userRepository.findAll().size() == existingUsers + 1
        def user = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).orElse(null).getUser()
        user != null
        and: 'is teaching'
        user.getCourseExecutions().size() == 1
        user.getCourseExecutions().stream().collect(Collectors.toList()).get(0).getAcronym() == COURSE_1_ACRONYM
    }

    def "no teacher does not have courses throw exception"() {
        given:
        client.getPersonName() >> USER_1_NAME
        client.getPersonUsername() >> USER_1_USERNAME
        client.getPersonEmail() >> USER_1_EMAIL
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        authUserService.fenixAuth(client)

        then:
        thrown(TutorException)
        and: 'the user is not created db'
        userRepository.findAll().size() == existingUsers
        and: 'no courses are created'
        courseRepository.findAll().size() == existingCourses
        courseExecutionRepository.findAll().size() == existingCourseExecutions
    }

    def "teacher has courses"() {
        given: 'a teacher'
        def user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        userRepository.save(user)

        and:
        client.getPersonName() >> USER_1_NAME
        client.getPersonUsername() >> USER_1_USERNAME
        client.getPersonEmail() >> USER_1_EMAIL
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> courses

        when:
        def result = authUserService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == USER_1_USERNAME
        result.user.name == USER_1_NAME
        result.user.email == USER_1_EMAIL
        and: 'the user is created in the db'
        userRepository.findAll().size() == existingUsers + 1
        authUserRepository.findAuthUserByUsername(USER_1_USERNAME).orElse(null) != null
        and: 'no courses are created'
        courseRepository.findAll().size() == existingCourses
        courseExecutionRepository.findAll().size() == existingCourseExecutions
    }

    def "teacher does not have courses"() {
        given: 'a teacher'
        def user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        userRepository.save(user)

        and:
        client.getPersonName() >> USER_1_NAME
        client.getPersonUsername() >> USER_1_USERNAME
        client.getPersonEmail() >> USER_1_EMAIL
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        def result = authUserService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == USER_1_USERNAME
        result.user.name == USER_1_NAME
        result.user.email == USER_1_EMAIL
        and: 'the user is created in the db'
        userRepository.findAll().size() == existingUsers + 1
        authUserRepository.findAuthUserByUsername(USER_1_USERNAME).orElse(null) != null
        and: 'no courses are created'
        courseRepository.findAll().size() == existingCourses
        courseExecutionRepository.findAll().size() == existingCourseExecutions
    }

    def "teacher has course and is in database, then add"() {
        given: 'a teacher'
        def user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        userRepository.save(user)

        client.getPersonName() >> USER_1_NAME
        client.getPersonUsername() >> USER_1_USERNAME
        client.getPersonEmail() >> USER_1_EMAIL
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> courses

        when:
        def result = authUserService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == USER_1_USERNAME
        result.user.name == USER_1_NAME
        result.user.email == USER_1_EMAIL
        and: 'the user is created in the db'
        userRepository.findAll().size() == existingUsers + 1
        def user2 = authUserRepository.findAuthUserByUsername(USER_1_USERNAME)
                .map({authUser -> authUser.getUser()})
                .orElse(null)
        user2 != null
        and: 'is teaching'
        user2.getCourseExecutions().size() == 1
        user2.getCourseExecutions().stream().collect(Collectors.toList()).get(0).getAcronym() == COURSE_1_ACRONYM
    }

    def "no student no courses, throw exception"() {
        given:
        client.getPersonName() >> USER_1_NAME
        client.getPersonUsername() >> USER_1_USERNAME
        client.getPersonEmail() >> USER_1_EMAIL
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        authUserService.fenixAuth(client)

        then:
        thrown(TutorException)
        and: 'the user is not created in the db'
        userRepository.findAll().size() == existingUsers
        and: 'no courses are created'
        courseRepository.findAll().size() == existingCourses
        courseExecutionRepository.findAll().size() == existingCourseExecutions
    }

    def "no student has courses but not in database, throw exception"() {
        userRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        courseRepository.deleteAll()

        client.getPersonName() >> USER_1_NAME
        client.getPersonUsername() >> USER_1_USERNAME
        client.getPersonEmail() >> USER_1_EMAIL
        client.getPersonAttendingCourses() >> courses
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        authUserService.fenixAuth(client)

        then:
        thrown(TutorException)
        and: 'the user is not created in the db'
        userRepository.findAll().size() == 0
        and: 'no courses are created'
        courseRepository.findAll().size() == 0
        courseExecutionRepository.findAll().size() == 0
    }

    def "no student has courses and in database, create student with attending course"() {
        client.getPersonName() >> USER_1_NAME
        client.getPersonUsername() >> USER_1_USERNAME
        client.getPersonEmail() >> USER_1_EMAIL
        client.getPersonAttendingCourses() >> courses
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        def result = authUserService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == USER_1_USERNAME
        result.user.name == USER_1_NAME
        result.user.email == USER_1_EMAIL
        and: 'the user is created in the db'
        userRepository.findAll().size() == existingUsers + 1
        User user = authUserRepository.findAuthUserByUsername(USER_1_USERNAME)
                .map({authUser -> authUser.getUser()})
                .orElse(null)
        user.getRole() == User.Role.STUDENT
        and: 'is enrolled'
        user.getCourseExecutions().size() == 1
        user.getCourseExecutions().stream().collect(Collectors.toList()).get(0).getAcronym() == COURSE_1_ACRONYM
    }

    def "student does not have courses and it is in the database"() {
        given: 'a student'
        def user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        userRepository.save(user)

        and:
        client.getPersonName() >> USER_1_NAME
        client.getPersonUsername() >> USER_1_USERNAME
        client.getPersonEmail() >> USER_1_EMAIL
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        authUserService.fenixAuth(client)

        then: 'the user is in the db'
        userRepository.findAll().size() == existingUsers + 1
        and: 'is not enrolled'
        user.getCourseExecutions().size() == 0
    }

    def "student has courses but they are not in the database, throw exception"() {
        userRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        courseRepository.deleteAll()

        given: 'a student'
        def user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        userRepository.save(user)

        and:
        client.getPersonName() >> USER_1_NAME
        client.getPersonUsername() >> USER_1_USERNAME
        client.getPersonEmail() >> USER_1_EMAIL
        client.getPersonAttendingCourses() >> courses
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        authUserService.fenixAuth(client)

        then: 'the user is in the db'
        userRepository.findAll().size() == 1
        and: 'is not enrolled'
        user.getCourseExecutions().size() == 0
    }

    def "student has courses and in the database, add course"() {
        given: 'a student'
        def user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        userRepository.save(user)

        client.getPersonName() >> USER_1_NAME
        client.getPersonUsername() >> USER_1_USERNAME
        client.getPersonEmail() >> USER_1_EMAIL
        client.getPersonAttendingCourses() >> courses
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        def result = authUserService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == USER_1_USERNAME
        result.user.name == USER_1_NAME
        result.user.email == USER_1_EMAIL
        and: 'the user is created in the db'
        userRepository.findAll().size() == existingUsers + 1
        authUserRepository.findAuthUserByUsername(USER_1_USERNAME).orElse(null).getUser().getRole() == User.Role.STUDENT
        and: 'is enrolled'
        user.getCourseExecutions().size() == 1
        user.getCourseExecutions().stream().collect(Collectors.toList()).get(0).getAcronym() == COURSE_1_ACRONYM
    }

    def "student has teaching courses"() {
        given: 'a student'
        def user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        userRepository.save(user)

        client.getPersonName() >> USER_1_NAME
        client.getPersonUsername() >> USER_1_USERNAME
        client.getPersonEmail() >> USER_1_EMAIL
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> courses

        when:
        authUserService.fenixAuth(client)

        then: 'the user is  in the db'
        userRepository.findAll().size() == existingUsers + 1
        authUserRepository.findAuthUserByUsername(USER_1_USERNAME).orElse(null).getUser().getRole() == User.Role.STUDENT
        and: 'but is not enrolled in the teaching courses'
        user.getCourseExecutions().size() == 0
    }

    def "teacher has attending courses, does not add course"() {
        given: 'a teacher'
        def user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        userRepository.save(user)

        client.getPersonName() >> USER_1_NAME
        client.getPersonUsername() >> USER_1_USERNAME
        client.getPersonEmail() >> USER_1_EMAIL
        client.getPersonAttendingCourses() >> courses
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        def result = authUserService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == USER_1_USERNAME
        result.user.name == USER_1_NAME
        result.user.email == USER_1_EMAIL
        and: 'the user is created in the db'
        userRepository.findAll().size() == existingUsers + 1
        authUserRepository.findAuthUserByUsername(USER_1_USERNAME).orElse(null).getUser().getRole() == User.Role.TEACHER
        and: 'is enrolled'
        user.getCourseExecutions().size() == 0
    }

    def "student has attending and teaching courses, add attending course"() {
        given: 'a teacher'
        def user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        userRepository.save(user)

        client.getPersonName() >> USER_1_NAME
        client.getPersonUsername() >> USER_1_USERNAME
        client.getPersonEmail() >> USER_1_EMAIL
        client.getPersonAttendingCourses() >> courses
        client.getPersonTeachingCourses() >> courses

        when:
        def result = authUserService.fenixAuth(client)

        then: "the returned data are correct"
        result.user.username == USER_1_USERNAME
        result.user.name == USER_1_NAME
        result.user.email == USER_1_EMAIL
        and: 'the user is created in the db'
        userRepository.findAll().size() == existingUsers + 1
        authUserRepository.findAuthUserByUsername(USER_1_USERNAME).orElse(null).getUser().getRole() == User.Role.TEACHER
        and: 'is enrolled'
        user.getCourseExecutions().size() == 1
        user.getCourseExecutions().stream().collect(Collectors.toList()).get(0).getAcronym() == COURSE_1_ACRONYM
    }

    def "username is associated with a different auth type, throw exception"() {
        given: 'a student'
        def user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        userRepository.save(user)

        and:
        client.getPersonName() >> USER_1_NAME
        client.getPersonUsername() >> USER_1_USERNAME
        client.getPersonEmail() >> USER_1_EMAIL
        client.getPersonAttendingCourses() >> courses
        client.getPersonTeachingCourses() >> new ArrayList<>()

        when:
        authUserService.fenixAuth(client)

        then: "the returned data are correct"
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.INVALID_AUTH_USERNAME
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
