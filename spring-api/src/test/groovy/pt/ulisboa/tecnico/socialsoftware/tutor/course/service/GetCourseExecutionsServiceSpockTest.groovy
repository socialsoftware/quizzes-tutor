package pt.ulisboa.tecnico.socialsoftware.tutor.course.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.TutorApplication
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Specification

@DataJpaTest
class GetCourseExecutionsServiceSpockTest extends Specification {
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
        def course = new Course(COURSE_ONE)
        courseRepository.save(course)
        and: "with two execution courses"
        def CourseExecution = new CourseExecution(course, ACRONYM_ONE, ACADEMIC_TERM_ONE)
        courseExecutionRepository.save(CourseExecution)
        CourseExecution = new CourseExecution(course, ACRONYM_TWO, ACADEMIC_TERM_TWO)
        courseExecutionRepository.save(CourseExecution)

        when:
        def result = courseService.getCourseExecutions(COURSE_ONE)

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
        courseService.getCourseExecutions("NO_COURSE")

        then: "the returned data are correct"
        TutorException ex = thrown()
    }

    def "no execution courses"() {
        given: "a course"
        def course = new Course(COURSE_ONE)
        courseRepository.save(course)

        when:
        def result = courseService.getCourseExecutions(COURSE_ONE)

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
