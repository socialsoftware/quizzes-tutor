package pt.ulisboa.tecnico.socialsoftware.tutor.execution.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.common.dtos.course.CourseType
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution

@DataJpaTest
class GetCourseExecutionsPerformanceTest extends SpockTest {
    def setup() {
        createExternalCourseAndExecution()
    }

    def "performance testing to get 1000 course executions"() {
        1.upto(1, {
            courseExecutionRepository.save(new CourseExecution(externalCourse, COURSE_1_ACRONYM + it, COURSE_1_ACADEMIC_TERM, CourseType.TECNICO, LOCAL_DATE_TOMORROW))
        })

        when:
        1.upto(1, { courseService.getCourseExecutions(Role.ADMIN)})

        then:
        true
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
