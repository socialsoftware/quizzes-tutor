package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException

@DataJpaTest
class RemoveTest extends SpockTest {
    def user

    def setup() {
        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        userRepository.save(user)
    }

    def "remove inactive user from course executions" (){
        given:
        def course = new Course(COURSE_2_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        def courseExecution = new CourseExecution(course, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)
        user.addCourse(courseExecution)
        def previousNumberOfUsers = courseExecution.getUsers().size()

        when:
        user.remove()

        then:
        courseExecution.getUsers().size() == previousNumberOfUsers - 1
    }

    def "remove active user from course executions" (){
        given:
        user.getAuthUser().setActive(true)
        and:
        def course = new Course(COURSE_2_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        def courseExecution = new CourseExecution(course, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)
        user.addCourse(courseExecution)
        def previousNumberOfUsers = courseExecution.getUsers().size()

        when:
        user.remove()

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.USER_IS_ACTIVE
        and:
        courseExecution.getUsers().size() == previousNumberOfUsers
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration { }
}
