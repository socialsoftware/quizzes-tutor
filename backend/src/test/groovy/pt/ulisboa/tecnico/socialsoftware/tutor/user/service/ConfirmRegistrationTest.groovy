package pt.ulisboa.tecnico.socialsoftware.tutor.user.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

@DataJpaTest
class ConfirmRegistrationTest extends SpockTest {

    @Autowired
    UserService userService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    static final String COURSE_NAME = "Course1"

    static final String ACRONYM = "Execution Acronym"
    static final String TERM = "19-20 Spring"

    static final String EMAIL = "test@mail.com"
    static final String PASSWORD = "123456abc"

    Course course
    CourseExecution courseExecution
    ExternalUserDto externalUserDto

    def setup(){
        course = new Course(COURSE_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, ACRONYM, TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)

        def executionId = courseExecution.getId()

        ExternalUserDto externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(EMAIL)
        externalUserDto.setUsername(EMAIL)
        externalUserDto.setRole(User.Role.STUDENT)

        userService.createExternalUser(executionId, externalUserDto)
    }

	def "user confirms registration successfully" () {
        externalUserDto.setPassword(PASSWORD)

        when:
        def result = userService.confirmRegistration(externalUserDto)

        then:"the user has a new password and matches"
        // TODO: use PasswordEncoder.matches
        and: "and his state is active"
        result.isActive() == true
	}

    def "user is already active" () {
        give: "an active user"
        externalUserDto.setPassword(PASSWORD)
        userService.confirmRegistration(externalUserDto)

        when:
        def result = userService.confirmRegistration(externalUserDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.USER_ALREADY_ACTIVE
    }

	def "registration confirmation unsuccessful" () {
        given: "a user"
        externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(email)
        externalUserDto.setUsername(email)
        externalUserDto.setPassword(password)

        when:
        def result = userService.confirmRegistration(externalUserDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == errorMessage

        where:
        email       | password      || errorMessage
        null        | PASSWORD      || ErrorMessage.USER_NOT_FOUND
        EMAIL       | null          || ErrorMessage.INVALID_PASSWORD
        EMAIL       | ""            || ErrorMessage.INVALID_PASSWORD
	}

}
