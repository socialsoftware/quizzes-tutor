package pt.ulisboa.tecnico.socialsoftware.auth.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.auth.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.auth.SpockTest
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.auth.services.FenixEduInterface
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.common.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

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
        def authUser = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).orElse(null)
        authUser.getUserSecurityInfo() != null
        and: 'is teaching'
        authUser.getUserCourseExecutions().size() == 1
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
        authUserService.createUserWithAuth(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, Role.TEACHER, AuthUser.Type.TECNICO)

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
        authUserService.createUserWithAuth(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, Role.TEACHER, AuthUser.Type.TECNICO)

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
        authUserService.createUserWithAuth(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, Role.TEACHER, AuthUser.Type.TECNICO)

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
        def authUser2 = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).orElse(null)
        def user2 = userRepository.findById(authUser2.getId()).orElse(null)
        user2 != null
        and: 'is teaching'
        user2.getCourseExecutions().size() == 1
        user2.getCourseExecutions().stream().collect(Collectors.toList()).get(0).getAcronym() == COURSE_1_ACRONYM
        authUser2.getUserCourseExecutions().size() == 1
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
        def authUser = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).orElse(null)
        def user = userRepository.findById(authUser.getUserSecurityInfo().getId()).orElse(null)
        user.getRole() == Role.STUDENT
        and: 'is enrolled'
        user.getCourseExecutions().size() == 1
        user.getCourseExecutions().stream().collect(Collectors.toList()).get(0).getAcronym() == COURSE_1_ACRONYM
        authUser.getUserCourseExecutions().size() == 1
    }

    def "student does not have courses and it is in the database"() {
        given: 'a student'
        def authUser = authUserService.createUserWithAuth(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, Role.STUDENT, AuthUser.Type.TECNICO)
        def user = userRepository.findById(authUser.getUserSecurityInfo().getId()).orElse(null)

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
        authUser.getUserCourseExecutions().size() == 0
        user.getCourseExecutions().size() == 0
    }

    def "student has courses but they are not in the database, throw exception"() {
        userRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        courseRepository.deleteAll()

        given: 'a student'
        def authUser = authUserService.createUserWithAuth(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, Role.STUDENT, AuthUser.Type.TECNICO)
        def user = userRepository.findById(authUser.getUserSecurityInfo().getId()).orElse(null)

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
        authUserService.createUserWithAuth(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, Role.STUDENT, AuthUser.Type.TECNICO)

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
        AuthUser authUser = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).orElse(null)
        User user = userRepository.findById(authUser.getUserSecurityInfo().getId()).orElse(null)
        authUser.getUserSecurityInfo().getRole() == Role.STUDENT
        and: 'is enrolled'
        user.getCourseExecutions().size() == 1
        user.getCourseExecutions().stream().collect(Collectors.toList()).get(0).getAcronym() == COURSE_1_ACRONYM
    }

    def "student has teaching courses"() {
        given: 'a student'
        authUserService.createUserWithAuth(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, Role.STUDENT, AuthUser.Type.TECNICO)

        client.getPersonName() >> USER_1_NAME
        client.getPersonUsername() >> USER_1_USERNAME
        client.getPersonEmail() >> USER_1_EMAIL
        client.getPersonAttendingCourses() >> new ArrayList<>()
        client.getPersonTeachingCourses() >> courses

        when:
        authUserService.fenixAuth(client)

        then: 'the user is  in the db'
        userRepository.findAll().size() == existingUsers + 1
        AuthUser authUser = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).orElse(null)
        User user = userRepository.findById(authUser.getUserSecurityInfo().getId()).orElse(null)
        authUser.getUserSecurityInfo().getRole() == Role.STUDENT
        and: 'but is not enrolled in the teaching courses'
        user.getCourseExecutions().size() == 0
    }

    def "teacher has attending courses, does not add course"() {
        given: 'a teacher'
        authUserService.createUserWithAuth(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, Role.TEACHER, AuthUser.Type.TECNICO)

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
        AuthUser authUser = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).orElse(null)
        User user = userRepository.findById(authUser.getUserSecurityInfo().getId()).orElse(null)
        authUser.getUserSecurityInfo().getRole() == Role.TEACHER
        and: 'is enrolled'
        user.getCourseExecutions().size() == 0
    }

    def "student has attending and teaching courses, add attending course"() {
        given: 'a teacher'
        authUserService.createUserWithAuth(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, Role.TEACHER, AuthUser.Type.TECNICO)

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
        AuthUser authUser = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).orElse(null)
        User user = userRepository.findById(authUser.getUserSecurityInfo().getId()).orElse(null)
        authUser.getUserSecurityInfo().getRole() == Role.TEACHER
        and: 'is enrolled'
        user.getCourseExecutions().size() == 1
        user.getCourseExecutions().stream().collect(Collectors.toList()).get(0).getAcronym() == COURSE_1_ACRONYM
    }

    def "username is associated with a different auth type, throw exception"() {
        given: 'a student'
        authUserService.createUserWithAuth(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, Role.STUDENT, AuthUser.Type.EXTERNAL)

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
