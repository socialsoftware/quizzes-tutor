package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution

@DataJpaTest
class AddCourseTest extends SpockTest {
    def user

    def setup() {
        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        userRepository.save(user)
    }

    def "addCourse" () {
        given:
        def course = new Course(COURSE_2_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        def courseExecution = new CourseExecution(course, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)
        def previousNumberOfUsers = courseExecution.getUsers().size()
        def previousNumberOfCourses = user.getCourseExecutions().size()

        when:
        user.addCourse(courseExecution)

        then:
        courseExecution.getUsers().size() == previousNumberOfUsers + 1
        user.getCourseExecutions().size() == previousNumberOfCourses + 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration { }
}
