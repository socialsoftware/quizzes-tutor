package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.common.dtos.course.CourseType
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course

@DataJpaTest
class AddCourseTest extends SpockTest {
    def user

    def setup() {
        user = new User(USER_1_NAME, USER_1_USERNAME, Role.STUDENT)
        userRepository.save(user)
    }

    def "addCourse" () {
        given:
        def course = new Course(COURSE_2_NAME, CourseType.TECNICO)
        courseRepository.save(course)
        def courseExecution = new CourseExecution(course, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, CourseType.TECNICO, LOCAL_DATE_TOMORROW)
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
