package pt.ulisboa.tecnico.socialsoftware.tutor.course.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
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
    static class LocalBeanConfiguration extends BeanConfiguration{}
}
