package pt.ulisboa.tecnico.socialsoftware.tutor.auth.service

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


@DataJpaTest
class ExternalUserAuthTest extends SpockTest {

    final static String PASSWORD = "1234"
    final static String WRONG_PASSWORD = "4321"

    User user
    Course course
    CourseExecution courseExecution
    ExternalUserDto externalUserDto

	def setup(){
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)

        user = new User(USER_1_NAME, USER_1_EMAIL, User.Role.STUDENT)
        user.addCourse(courseExecution)
        user.setState(User.State.ACTIVE)
        user.setPassword(passwordEncoder.encode(PASSWORD))
        courseExecution.addUser(user)
        userRepository.save(user)

        externalUserDto = new ExternalUserDto()
        externalUserDto.setUsername(USER_1_EMAIL)
    }

    def "user logins successfully" () {
        given: "an user with a correct password"
        externalUserDto.setPassword(PASSWORD)

        when:
        def result = authService.externalUserAuth(externalUserDto)

        then:
        result.user.username == USER_1_EMAIL
    }

    def "login fails, given values are invalid" () {
        given: "an user with a correct password"
        externalUserDto.setUsername(username)
        externalUserDto.setPassword(password)

        when:
        authService.externalUserAuth(externalUserDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == errorMessage

        where:
        username    | password        || errorMessage
        USER_2_EMAIL | PASSWORD       || ErrorMessage.EXTERNAL_USER_NOT_FOUND
        USER_1_EMAIL | WRONG_PASSWORD || ErrorMessage.INVALID_PASSWORD
        USER_1_EMAIL | null           || ErrorMessage.INVALID_PASSWORD
    }
    

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
