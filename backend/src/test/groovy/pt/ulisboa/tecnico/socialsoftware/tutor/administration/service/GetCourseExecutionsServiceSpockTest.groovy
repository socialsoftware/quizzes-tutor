package pt.ulisboa.tecnico.socialsoftware.tutor.administration.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.administration.AdministrationService
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class GetCourseExecutionsServiceSpockTest extends Specification {
    static final String COURSE_ONE = "CourseOne"
    static final String ACRONYM_ONE = "C12"
    static final String ACADEMIC_TERM_ONE = "1º Semestre"
    static final String ACRONYM_TWO = "C22"
    static final String ACADEMIC_TERM_TWO = "2º Semestre"

    @Autowired
    AdministrationService administrationService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def "the tecnico course exists and create execution course"() {
        given: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "two course executions"
        def courseExecution = new CourseExecution(course, ACRONYM_ONE, ACADEMIC_TERM_ONE, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        courseExecution = new CourseExecution(course, ACRONYM_TWO, ACADEMIC_TERM_TWO, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)

        when:
        def result = administrationService.getCourseExecutions()

        then: "the returned data are correct"
        result.size() == 2
        def tecnicoExecutionCourse = result.get(0)
        tecnicoExecutionCourse.name == COURSE_ONE
        tecnicoExecutionCourse.courseType == Course.Type.TECNICO
        tecnicoExecutionCourse.acronym == ACRONYM_ONE
        tecnicoExecutionCourse.academicTerm == ACADEMIC_TERM_ONE
        tecnicoExecutionCourse.courseExecutionType == Course.Type.TECNICO
        def externalExecutionCourse = result.get(1)
        externalExecutionCourse.name == COURSE_ONE
        externalExecutionCourse.courseType == Course.Type.TECNICO
        externalExecutionCourse.acronym == ACRONYM_TWO
        externalExecutionCourse.academicTerm == ACADEMIC_TERM_TWO
        externalExecutionCourse.courseExecutionType == Course.Type.EXTERNAL
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        AdministrationService administrationService() {
            return new AdministrationService()
        }

    }
}
