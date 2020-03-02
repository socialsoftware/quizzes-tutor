package pt.ulisboa.tecnico.socialsoftware.tutor.course.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Specification

@DataJpaTest
class GetCourseExecutionsServiceTest extends Specification {
    public static final String COURSE_ONE = "CourseOne"
    public static final String ACRONYM_ONE = "C12"
    public static final String ACRONYM_TWO = "C11"
    public static final String ACADEMIC_TERM_ONE = "1º Semestre"
    public static final String ACADEMIC_TERM_TWO = "2º Semestre"

    @Autowired
    CourseService courseService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def "two execution courses"() {
        given: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "with two execution courses"
        def CourseExecution = new CourseExecution(course, ACRONYM_ONE, ACADEMIC_TERM_ONE, Course.Type.TECNICO)
        courseExecutionRepository.save(CourseExecution)
        CourseExecution = new CourseExecution(course, ACRONYM_TWO, ACADEMIC_TERM_TWO, Course.Type.TECNICO)
        courseExecutionRepository.save(CourseExecution)

        when:
        def result = courseService.getCourseExecutions(course.getId())

        then: "the returned data are correct"
        result.size() == 2
        and: "correctly reverse sorted"
        result.get(0).name == COURSE_ONE
        result.get(0).acronym == ACRONYM_TWO
        result.get(0).academicTerm == ACADEMIC_TERM_TWO
        result.get(1).name == COURSE_ONE
        result.get(1).acronym == ACRONYM_ONE
        result.get(1).academicTerm == ACADEMIC_TERM_ONE
    }

    def "does not exist course with the name"() {
        when:
        courseService.getCourseExecutions(0)

        then: "the returned data are correct"
        TutorException ex = thrown()
    }

    def "no execution courses"() {
        given: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)

        when:
        def result = courseService.getCourseExecutions(course.getId())

        then: "the returned data are correct"
        result.size() == 0
    }


    @TestConfiguration
    static class CourseServiceImplTestContextConfiguration {

        @Bean
        CourseService courseService() {
            return new CourseService()
        }
    }

}
