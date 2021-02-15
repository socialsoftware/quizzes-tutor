package pt.ulisboa.tecnico.socialsoftware.tutor.user.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.Mailer
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthExternalUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto
import spock.lang.Unroll
import spock.mock.DetachedMockFactory

@DataJpaTest
class RegisterExternalUserTest extends SpockTest {
    def externalUserDto
    def previousNumberUser

    @Autowired
    Mailer mailerMock

    def setup() {
        externalCourse = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(externalCourse)
        externalCourseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(externalCourseExecution)

        previousNumberUser = userRepository.findAll().size()
    }

    def "the course execution exists, the external user is enrolled in it and tries to enroll again"() {
        given: "a external course execution"
        def executionId = externalCourseExecution.getId()
        and: "a external user dto"
        externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(USER_1_EMAIL)
        externalUserDto.setRole(User.Role.STUDENT)
        and: "an already created user"
        def user = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        ((AuthExternalUser)user.authUser).setActive(true)
        userRepository.save(user)
        and: 'enrolled'
        user.addCourse(externalCourseExecution)

        when:
        userServiceApplicational.registerExternalUser(executionId, externalUserDto)

        then: "an exception"
        thrown(TutorException)
        and: 'a user in the database'
        def authUser = authUserRepository.findAuthUserByUsername(USER_1_EMAIL).orElse(null)
        authUser != null
        and: 'it is not enrolled twice'
        externalCourseExecution.getUsers().contains(authUser.getUser())
        and: "no mail is sent"
        0 * mailerMock.sendSimpleMail(mailerUsername,_,_,_)
    }

    def "the course execution exists, the username does not exist, create the user and associate the user with the course execution"() {
        given: "a external course execution"
        def executionId = externalCourseExecution.getId()
        and: "a external user dto"
        externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(USER_1_EMAIL)
        externalUserDto.setRole(User.Role.STUDENT)

        when:
        def result = userServiceApplicational.registerExternalUser(executionId, externalUserDto)

        then: "the user is saved in the database"
        userRepository.findAll().size() == previousNumberUser + 1
        and: "checks if user data is correct"
        result.getUsername() == USER_1_EMAIL
        result.getEmail() == USER_1_EMAIL
        !result.getActive()
        and: "checks if the user and the course execution are associated"
        result.getConfirmationToken() != ""
        externalCourseExecution.getUsers().size() == 1
        externalCourseExecution.getUsers().toList().get(0).getId() == result.getId()
        and: "a mail is sent"
        1 * mailerMock.sendSimpleMail(mailerUsername, USER_1_EMAIL, Mailer.QUIZZES_TUTOR_SUBJECT + userService.PASSWORD_CONFIRMATION_MAIL_SUBJECT, _)
    }

    def "the course execution exists, the user exists but he's not enrolled and enroll him in the course execution"() {
        given: "a user"
        def user = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        userRepository.save(user)
        ((AuthExternalUser)user.authUser).setActive(true)

        and: "a external course execution"
        def executionId = externalCourseExecution.getId()
        and: "a external user dto"
        externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(USER_1_EMAIL)
        externalUserDto.setRole(User.Role.STUDENT)

        when:
        def result = userServiceApplicational.registerExternalUser(executionId, externalUserDto)

        then:"the user is saved in the database"
        userRepository.count() == previousNumberUser + 1
        and: "checks if user data is correct"
        result.getUsername() == USER_1_EMAIL
        result.getEmail() == USER_1_EMAIL
        result.getActive()
        and:"checks if the user and the course execution are associated"
        result.getConfirmationToken() != ""
        externalCourseExecution.getUsers().size() == 1
        externalCourseExecution.getUsers().toList().get(0).getId() == result.getId()

        and: "no mail is sent"
        0 * mailerMock.sendSimpleMail(mailerUsername,_,_,_)
    }

    @Unroll
    def "invalid arguments: executionType=#executionType | email=#email | role=#role"() {
        given: "a course execution id"
        externalCourseExecution.setType(executionType)
        def executionId = externalCourseExecution.getId()
        and: "a external user dto"
        externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(email)
        externalUserDto.setRole(role)

        when:
        userServiceApplicational.registerExternalUser(executionId, externalUserDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == errorMessage
        and: "no user was created"
        userRepository.count() == previousNumberUser
        and: "no mail is sent"
        0 * mailerMock.sendSimpleMail(mailerUsername,_,_,_)

        where:
        executionType         | email             | role                     || errorMessage
        Course.Type.TECNICO   | USER_1_EMAIL      | User.Role.STUDENT        || ErrorMessage.COURSE_EXECUTION_NOT_EXTERNAL
        Course.Type.EXTERNAL  | null              | User.Role.STUDENT        || ErrorMessage.INVALID_EMAIL
        Course.Type.EXTERNAL  | ""                | User.Role.STUDENT        || ErrorMessage.INVALID_EMAIL
        Course.Type.EXTERNAL  | "test.mail.com"   | User.Role.STUDENT        || ErrorMessage.INVALID_EMAIL
        Course.Type.EXTERNAL  | "test@"           | User.Role.STUDENT        || ErrorMessage.INVALID_EMAIL
        Course.Type.EXTERNAL  | USER_1_EMAIL      | null                     || ErrorMessage.INVALID_ROLE
        Course.Type.EXTERNAL  | USER_1_EMAIL      | User.Role.ADMIN          || ErrorMessage.INVALID_ROLE
        Course.Type.EXTERNAL  | USER_1_EMAIL      | User.Role.DEMO_ADMIN     || ErrorMessage.INVALID_ROLE

    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {
        def mockFactory = new DetachedMockFactory()

        @Bean
        Mailer mailer() {
            return mockFactory.Mock(Mailer)
        }
    }
}