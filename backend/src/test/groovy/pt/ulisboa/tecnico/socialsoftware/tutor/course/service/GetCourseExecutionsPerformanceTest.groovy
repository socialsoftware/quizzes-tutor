package pt.ulisboa.tecnico.socialsoftware.tutor.course.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.*
import spock.lang.Specification

@DataJpaTest
class GetCourseExecutionsPerformanceTest extends Specification {
    static final String COURSE = "CourseOne"
    static final String ACRONYM = "C12"
    static final String ACADEMIC_TERM = "1º Semestre"

    @Autowired
    CourseService courseService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

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
    static class ServiceImplTestContextConfiguration {

        @Bean
        CourseService courseService() {
            return new CourseService()
        }
    }
}
