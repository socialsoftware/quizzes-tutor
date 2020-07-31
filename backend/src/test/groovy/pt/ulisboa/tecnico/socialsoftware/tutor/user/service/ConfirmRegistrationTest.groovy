package pt.ulisboa.tecnico.socialsoftware.tutor.user.service

import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.mailer.Mailer
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto
import spock.mock.DetachedMockFactory

import java.time.LocalDateTime;

@DataJpaTest
class ConfirmRegistrationTest extends SpockTest {

    Course course
    CourseExecution courseExecution
    ExternalUserDto externalUserDto

    def setup(){
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)

        def executionId = courseExecution.getId()

        externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(USER_1_EMAIL)
        externalUserDto.setUsername(USER_1_EMAIL)
        externalUserDto.setConfirmationToken(USER_1_TOKEN)
        externalUserDto.setRole(User.Role.STUDENT)

        userService.createExternalUser(executionId, externalUserDto)

        User user = userService.findByUsername(USER_1_EMAIL)
        user.setConfirmationToken(USER_1_TOKEN)
    }

	def "user confirms registration successfully" () {
        given: "a password"
        externalUserDto.setPassword(USER_1_PASSWORD)

        when:
        def result = userService.confirmRegistration(externalUserDto)

        then:"the user has a new password and matches"
        passwordEncoder.matches(USER_1_PASSWORD, result.getPassword())
        and: "and is active"
        result.isActive()
	}

    def "user is already active" () {
        given: "an active user"
        externalUserDto.setPassword(USER_1_PASSWORD)
        userService.confirmRegistration(externalUserDto)

        when:
        userService.confirmRegistration(externalUserDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.USER_ALREADY_ACTIVE
    }

    def "user token expired" () {
        given: "a new password"
        externalUserDto.setPassword(USER_1_PASSWORD)
        and: "and an expired token generation date"
        User user = userService.findByUsername(USER_1_EMAIL)
        user.setTokenGenerationDate(LocalDateTime.now().minusDays(1).minusMinutes(1))

        when:
        def result = userService.confirmRegistration(externalUserDto)

        then:
        result.state == User.State.INACTIVE
        and: "a new token is created"
        result.confirmationToken != USER_1_TOKEN
    }

	def "registration confirmation unsuccessful" () {
        given: "a user"
        externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(email)
        externalUserDto.setUsername(email)
        externalUserDto.setConfirmationToken(token)
        externalUserDto.setPassword(password)

        when:
        userService.confirmRegistration(externalUserDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == errorMessage

        where:
        email        | password         | token           || errorMessage
        ""           | USER_1_PASSWORD  | USER_1_TOKEN    || ErrorMessage.EXTERNAL_USER_NOT_FOUND
        null         | USER_1_PASSWORD  | USER_1_TOKEN    || ErrorMessage.EXTERNAL_USER_NOT_FOUND
        USER_1_EMAIL | null             | USER_1_TOKEN    || ErrorMessage.INVALID_PASSWORD
        USER_1_EMAIL | ""               | USER_1_TOKEN    || ErrorMessage.INVALID_PASSWORD
        USER_1_EMAIL | USER_1_PASSWORD  | ""              || ErrorMessage.INVALID_CONFIRMATION_TOKEN
	}


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {
        def mockFactory = new DetachedMockFactory()

        @Bean
        Mailer mailer(){
            return mockFactory.Mock(Mailer)
        }
    }
}
