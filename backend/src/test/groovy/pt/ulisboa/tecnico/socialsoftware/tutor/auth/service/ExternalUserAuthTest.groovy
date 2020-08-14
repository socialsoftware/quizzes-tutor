package pt.ulisboa.tecnico.socialsoftware.tutor.auth.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User


@DataJpaTest
class ExternalUserAuthTest extends SpockTest {

    User user
    Course course
    CourseExecution courseExecution

	def setup(){
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)

        user = new User(USER_1_NAME, USER_1_EMAIL, User.Role.STUDENT)
        user.addCourse(courseExecution)
        user.setState(User.State.ACTIVE)
        user.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        courseExecution.addUser(user)
        userRepository.save(user)

    }

    def "user logins successfully" () {
        when:
        def result = authService.externalUserAuth(USER_1_EMAIL, USER_1_PASSWORD)

        then:
        result.user.username == USER_1_EMAIL
    }

    def "login fails, given values are invalid" () {
        when:
        authService.externalUserAuth(username, password)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == errorMessage

        where:
        username     | password        || errorMessage
        null         | USER_1_PASSWORD || ErrorMessage.EXTERNAL_USER_NOT_FOUND
        USER_2_EMAIL | USER_1_PASSWORD || ErrorMessage.EXTERNAL_USER_NOT_FOUND
        USER_1_EMAIL | USER_2_PASSWORD || ErrorMessage.INVALID_PASSWORD
        USER_1_EMAIL | null            || ErrorMessage.INVALID_PASSWORD
    }
    

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
