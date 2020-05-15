package pt.ulisboa.tecnico.socialsoftware.tutor.course.service


import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class GetCourseExecutionsPerformanceTest extends SpockTest {
    static final String COURSE = "CourseOne"
    static final String ACRONYM = "C12"
    static final String ACADEMIC_TERM = "1º Semestre"

    def "performance testing to get 1000 course executions"() {
        given: "a course"
        def course = new Course(COURSE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a 1000 course executions"
        1.upto(1, {
            courseExecutionRepository.save(new CourseExecution(course, ACRONYM + it, ACADEMIC_TERM, Course.Type.TECNICO))
        })

        when:
        1.upto(1, { courseService.getCourseExecutions(User.Role.ADMIN)})

        then:
        true
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
