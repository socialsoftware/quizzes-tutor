package pt.ulisboa.tecnico.socialsoftware.tutor.course.domain

import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException

class CreateCourseExecutionsTest extends SpockTest {
    static final String ACRONYM_ONE = "C12"
    static final String ACADEMIC_TERM_ONE = "1º Semestre"

    def "the tecnico course exists and create external execution course"() {
        given: "a course"
        def course = Mock(Course)
        and: "it does not have course executions"
        course.existsCourseExecution(ACRONYM_ONE, ACADEMIC_TERM_ONE, Course.Type.EXTERNAL) >> false

        when:
        def courseExecution = new CourseExecution(course, ACRONYM_ONE, ACADEMIC_TERM_ONE, Course.Type.EXTERNAL)

        then: "the object is correctly created"
        courseExecution.acronym == ACRONYM_ONE
        courseExecution.academicTerm == ACADEMIC_TERM_ONE
        courseExecution.type == Course.Type.EXTERNAL
        1 * course.addCourseExecution(_)
    }

    def "exist create external execution course with the same name"() {
        given: "a course"
        def course = Mock(Course)
        and: "course execution already exists"
        course.existsCourseExecution(ACRONYM_ONE, ACADEMIC_TERM_ONE, Course.Type.EXTERNAL) >> true

        when:
        new CourseExecution(course, ACRONYM_ONE, ACADEMIC_TERM_ONE, Course.Type.EXTERNAL)

        then: "the returned data are correct"
        thrown(TutorException)
        and: "it is not added to course"
        0 * course.addCourseExecution(_)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
