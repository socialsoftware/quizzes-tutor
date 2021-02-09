package pt.ulisboa.tecnico.socialsoftware.tutor.auth.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser

@DataJpaTest
class ExternalUserAuthTest extends SpockTest {

    User user
    AuthUser authUser
    Course course
    CourseExecution courseExecution

	def setup(){
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        user = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        user.addCourse(courseExecution)
        user.getAuthUser().setActive(true)
        courseExecution.addUser(user)
        userRepository.save(user)
        user.getAuthUser().setPassword(passwordEncoder.encode(USER_1_PASSWORD))
    }

    def "user logins successfully" () {
        when:
        def result = authUserService.externalUserAuth(USER_1_EMAIL, USER_1_PASSWORD)

        then:
        result.user.username == USER_1_EMAIL
    }

    def "login fails, given values are invalid" () {
        when:
        authUserService.externalUserAuth(username, password)

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
