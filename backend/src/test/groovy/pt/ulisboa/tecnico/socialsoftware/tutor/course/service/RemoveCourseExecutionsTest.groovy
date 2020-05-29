package pt.ulisboa.tecnico.socialsoftware.tutor.course.service


import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz

@DataJpaTest
class RemoveCourseExecutionsTest extends SpockTest {
    static final String COURSE_ONE = "CourseOne"
    static final String ACRONYM_ONE = "C12"
    static final String ACADEMIC_TERM_ONE = "1º Semestre"
    static final String ACRONYM_TWO = "C22"
    static final String ACADEMIC_TERM_TWO = "2º Semestre"

    def courseExecutionTecnico
    def courseExecutionExternal

    def existingCourseExecutions

    def setup() {
        existingCourseExecutions = courseExecutionRepository.findAll().size()

        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "two course executions"
        courseExecutionTecnico = new CourseExecution(course, ACRONYM_ONE, ACADEMIC_TERM_ONE, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecutionTecnico)
        courseExecutionExternal = new CourseExecution(course, ACRONYM_TWO, ACADEMIC_TERM_TWO, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecutionExternal)
     }

    def "delete a execution course"() {
        when:
        courseService.removeCourseExecution(courseExecutionExternal.id)

        then: "the returned data are correct"
        courseExecutionRepository.findAll().size() == existingCourseExecutions + 1
        def tecnicoExecutionCourse = courseExecutionRepository.findByFields(ACRONYM_ONE, ACADEMIC_TERM_ONE, Course.Type.TECNICO.name()).get()
        tecnicoExecutionCourse.acronym == ACRONYM_ONE
        tecnicoExecutionCourse.academicTerm == ACADEMIC_TERM_ONE
        tecnicoExecutionCourse.type == Course.Type.TECNICO
    }

    def "cannot delete a execution course with quizzes"() {
        given: "tecnico course has quizzes"
        def quiz = new Quiz()
        quiz.setKey(1)
        quiz.setCourseExecution(courseExecutionTecnico)
        courseExecutionTecnico.addQuiz(quiz)
        quizRepository.save(quiz)

        when:
        courseService.removeCourseExecution(courseExecutionTecnico.id)

        then: "the returned data are correct"
        thrown(TutorException)
        courseExecutionRepository.findAll().size() == existingCourseExecutions + 2
    }

    def "cannot delete a execution course with assessments"() {
        given: "external course has assessments"
        def assessment = new Assessment()
        assessment.setTitle("Assessment Title")
        assessment.setCourseExecution(courseExecutionExternal)
        courseExecutionExternal.addAssessment(assessment)
        assessmentRepository.save(courseExecutionExternal)

        when:
        courseService.removeCourseExecution(courseExecutionExternal.id)

        then: "the returned data are correct"
        thrown(TutorException)
        courseExecutionRepository.findAll().size() == existingCourseExecutions + 2
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
