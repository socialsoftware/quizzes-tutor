package pt.ulisboa.tecnico.socialsoftware.tutor.user.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.password.PasswordEncoder
import pt.ulisboa.tecnico.socialsoftware.tutor.mailer.Mailer
import spock.lang.Unroll
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


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Mailer mailerMock;

    static final String COURSE_NAME = "Course1"

    static final String ACRONYM = "Execution Acronym"
    static final String TERM = "19-20 Spring"

    static final String EMAIL = "test@mail.com"
    static final String PASSWORD = "123456abc"
    static final String TOKEN = "12345"

    Course course
    CourseExecution courseExecution
    ExternalUserDto externalUserDto

    def setup(){
        course = new Course(COURSE_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, ACRONYM, TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)

        def executionId = courseExecution.getId()

        externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(EMAIL)
        externalUserDto.setUsername(EMAIL)
        externalUserDto.setConfirmationToken(TOKEN)
        externalUserDto.setRole(User.Role.STUDENT)

        userService.createExternalUser(executionId, externalUserDto)

        User user = userService.findByUsername(EMAIL)
        user.setConfirmationToken(TOKEN)
    }

	def "user confirms registration successfully" () {
        externalUserDto.setPassword(PASSWORD)

        when:
        def result = userService.confirmRegistration(externalUserDto)

        then:"the user has a new password and matches"
        passwordEncoder.matches(PASSWORD, result.getPassword())
        and: "and is active"
        result.isActive()
        and: "no email is sent"
        0 * mailerMock.sendSimpleMail(_,_,_,_)
	}

    def "user is already active" () {
        given: "an active user"
        externalUserDto.setPassword(PASSWORD)
        userService.confirmRegistration(externalUserDto)

        when:
        userService.confirmRegistration(externalUserDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.USER_ALREADY_ACTIVE
        and: "no email is sent"
        0 * mailerMock.sendSimpleMail(_,_,_,_)
    }

    def "user token expired" () {
        given: "a new password"
        externalUserDto.setPassword(PASSWORD)
        and: "and an expired token generation date"
        User user = userService.findByUsername(EMAIL)
        user.setTokenGenerationDate(LocalDateTime.now().minusDays(1).minusMinutes(1))

        when:
        def result = userService.confirmRegistration(externalUserDto)

        then:
        result.state == User.State.INACTIVE
        and: "a new token is created"
        result.confirmationToken != TOKEN
        and: "a new email is sent"
        1 * mailerMock.sendSimpleMail(_, EMAIL,_,_)
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
        and: "no email is sent"
        0 * mailerMock.sendSimpleMail(_,_,_,_)

        where:
        email       | password  | token     || errorMessage
        null        | PASSWORD  | TOKEN     || ErrorMessage.EXTERNAL_USER_NOT_FOUND
        EMAIL       | null      | TOKEN     || ErrorMessage.INVALID_PASSWORD
        EMAIL       | ""        | TOKEN     || ErrorMessage.INVALID_PASSWORD
        EMAIL       | PASSWORD  | ""        || ErrorMessage.INVALID_CONFIRMATION_TOKEN
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
