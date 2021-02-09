package pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course

@DataJpaTest
class CreateCourseExecutionsTest extends SpockTest {

    def "the tecnico course exists and create external execution course"() {
        given: "a course"
        def course = Mock(Course)
        and: "it does not have course executions"
        course.existsCourseExecution(COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL) >> false

        when:
        def courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)

        then: "the object is correctly created"
        courseExecution.acronym == COURSE_1_ACRONYM
        courseExecution.academicTerm == COURSE_1_ACADEMIC_TERM
        courseExecution.type == Course.Type.EXTERNAL
        1 * course.addCourseExecution(_)
    }

    def "exist create external execution course with the same name"() {
        given: "a course"
        def course = Mock(Course)
        and: "course execution already exists"
        course.existsCourseExecution(COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL) >> true

        when:
        new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)

        then: "the returned data are correct"
        thrown(TutorException)
        and: "it is not added to course"
        0 * course.addCourseExecution(_)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
