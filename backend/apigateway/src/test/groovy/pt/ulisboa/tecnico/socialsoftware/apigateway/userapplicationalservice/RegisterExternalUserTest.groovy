package pt.ulisboa.tecnico.socialsoftware.apigateway.userapplicationalservice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.apigateway.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.apigateway.SpockTest
import pt.ulisboa.tecnico.socialsoftware.apigateway.auth.domain.AuthExternalUser
import pt.ulisboa.tecnico.socialsoftware.apigateway.auth.domain.UserSecurityInfo
import pt.ulisboa.tecnico.socialsoftware.common.dtos.course.CourseType
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.ExternalUserDto
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.common.utils.Mailer
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import spock.lang.Unroll
import spock.mock.DetachedMockFactory

@DataJpaTest
class RegisterExternalUserTest extends SpockTest {
    def externalUserDto
    def previousNumberUser

    @Autowired
    Mailer mailerMock

    def setup() {
        externalCourse = new Course(COURSE_1_NAME, CourseType.EXTERNAL)
        courseRepository.save(externalCourse)
        externalCourseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, CourseType.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(externalCourseExecution)

        previousNumberUser = userRepository.findAll().size()
    }

    def "the course execution exists, the external user is enrolled in it and tries to enroll again"() {
        given: "a external course execution"
        def executionId = externalCourseExecution.getId()
        and: "a external user dto"
        externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(USER_1_EMAIL)
        externalUserDto.setRole(Role.STUDENT)
        and: "an already created user"
        def user = new User(USER_1_NAME, USER_1_EMAIL, Role.STUDENT, false)
        user.setActive(true)
        userRepository.save(user)
        def authUser = new AuthExternalUser(new UserSecurityInfo(user.getId(), USER_1_NAME, Role.STUDENT, false), USER_1_EMAIL, USER_1_EMAIL)
        authUser.addCourseExecution(externalCourseExecution.getId())
        authUser.setActive(true)
        authUserRepository.save(authUser)
        and: 'enrolled'
        user.addCourse(externalCourseExecution)

        when:
        userServiceApplicational.registerExternalUser(executionId, externalUserDto)

        then: "an exception"
        thrown(TutorException)
        and: 'a user in the database'
        def result = authUserRepository.findAuthUserByUsername(USER_1_EMAIL).orElse(null)
        result != null
        and: 'it is not enrolled twice'
        externalCourseExecution.getUsers().contains(user)
        and: "no mail is sent"
        0 * mailerMock.sendSimpleMail(mailerUsername, _, _, _)
    }

    def "the course execution exists, the username does not exist, create the user and associate the user with the course execution"() {
        given: "a external course execution"
        def executionId = externalCourseExecution.getId()
        and: "a external user dto"
        externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(USER_1_EMAIL)
        externalUserDto.setRole(Role.STUDENT)

        when:
        def result = userServiceApplicational.registerExternalUser(executionId, externalUserDto)

        then: "the user is saved in the database"
        userRepository.findAll().size() == previousNumberUser + 1
        and: "checks if user data is correct"
        result.getUsername() == USER_1_EMAIL
        result.getEmail() == USER_1_EMAIL
        !result.isActive()
        and: "checks if the user and the course execution are associated"
        result.getConfirmationToken() != ""
        externalCourseExecution.getUsers().size() == 1
        externalCourseExecution.getUsers().toList().get(0).getId() == result.getId()
        and: "a mail is sent"
        1 * mailerMock.sendSimpleMail(mailerUsername, USER_1_EMAIL, Mailer.QUIZZES_TUTOR_SUBJECT + userService.PASSWORD_CONFIRMATION_MAIL_SUBJECT, _)
    }

    def "the course execution exists, the user exists but he's not enrolled and enroll him in the course execution"() {
        given: "a user"
        def user = new User(USER_1_NAME, USER_1_EMAIL, Role.STUDENT, false)
        user.setActive(true)
        userRepository.save(user)
        def authUser = new AuthExternalUser(new UserSecurityInfo(user.getId(), USER_1_NAME, Role.STUDENT, false), USER_1_EMAIL, USER_1_EMAIL)
        authUser.setActive(true)
        authUserRepository.save(authUser)

        and: "a external course execution"
        def executionId = externalCourseExecution.getId()
        and: "a external user dto"
        externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(USER_1_EMAIL)
        externalUserDto.setRole(Role.STUDENT)

        when:
        def result = userServiceApplicational.registerExternalUser(executionId, externalUserDto)

        then:"the user is saved in the database"
        userRepository.count() == previousNumberUser + 1
        and: "checks if user data is correct"
        result.getUsername() == USER_1_EMAIL
        result.getEmail() == USER_1_EMAIL
        result.isActive()
        and:"checks if the user and the course execution are associated"
        result.getConfirmationToken() != ""
        externalCourseExecution.getUsers().size() == 1
        externalCourseExecution.getUsers().toList().get(0).getId() == result.getId()

        and: "no mail is sent"
        0 * mailerMock.sendSimpleMail(mailerUsername, _, _, _)
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
        0 * mailerMock.sendSimpleMail(mailerUsername, _, _, _)

        where:
        executionType         | email            | role            || errorMessage
        CourseType.TECNICO   | USER_1_EMAIL      | Role.STUDENT    || ErrorMessage.COURSE_EXECUTION_NOT_EXTERNAL
        CourseType.EXTERNAL  | null              | Role.STUDENT    || ErrorMessage.INVALID_EMAIL
        CourseType.EXTERNAL  | ""                | Role.STUDENT    || ErrorMessage.INVALID_EMAIL
        CourseType.EXTERNAL  | "test.mail.com"   | Role.STUDENT    || ErrorMessage.INVALID_EMAIL
        CourseType.EXTERNAL  | "test@"           | Role.STUDENT    || ErrorMessage.INVALID_EMAIL
        CourseType.EXTERNAL  | USER_1_EMAIL      | null            || ErrorMessage.INVALID_ROLE
        CourseType.EXTERNAL  | USER_1_EMAIL      | Role.ADMIN      || ErrorMessage.INVALID_ROLE
        CourseType.EXTERNAL  | USER_1_EMAIL      | Role.DEMO_ADMIN || ErrorMessage.INVALID_ROLE

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