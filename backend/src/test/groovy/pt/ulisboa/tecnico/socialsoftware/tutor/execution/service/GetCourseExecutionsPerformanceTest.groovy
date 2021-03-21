package pt.ulisboa.tecnico.socialsoftware.tutor.execution.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@DataJpaTest
class GetCourseExecutionsPerformanceTest extends SpockTest {
    def setup() {
        createExternalCourseAndExecution()
    }

    def "performance testing to get 1000 course executions"() {
        1.upto(1, {
            courseExecutionRepository.save(new CourseExecution(externalCourse, COURSE_1_ACRONYM + it, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TOMORROW))
        })

        when:
        1.upto(1, { courseService.getCourseExecutions(User.Role.ADMIN)})

        then:
        true
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
