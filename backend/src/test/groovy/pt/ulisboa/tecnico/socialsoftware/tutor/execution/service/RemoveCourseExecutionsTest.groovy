package pt.ulisboa.tecnico.socialsoftware.tutor.execution.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz

@DataJpaTest
class RemoveCourseExecutionsTest extends SpockTest {
    def courseExecutionTecnico
    def courseExecutionExternal
    def existingCourseExecutions

    def setup() {
        createExternalCourseAndExecution()

        existingCourseExecutions = courseExecutionRepository.findAll().size()

        courseExecutionTecnico = externalCourseExecution

        courseExecutionExternal = new CourseExecution(externalCourse, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecutionExternal)
     }

    def "delete a execution course"() {
        when:
        courseService.removeCourseExecution(courseExecutionExternal.id)

        then: "the returned data are correct"
        courseExecutionRepository.findAll().size() == existingCourseExecutions
        def tecnicoExecutionCourse = courseExecutionRepository.findByFields(COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO.name()).get()
        tecnicoExecutionCourse.acronym == COURSE_1_ACRONYM
        tecnicoExecutionCourse.academicTerm == COURSE_1_ACADEMIC_TERM
        tecnicoExecutionCourse.type == Course.Type.TECNICO
    }

    def "cannot delete a execution course with quizzes"() {
        given: "tecnico course has quizzes"
        Quiz quiz = new Quiz()
        quiz.setKey(1)
        quiz.setCourseExecution(courseExecutionTecnico)
        courseExecutionTecnico.addQuiz(quiz)
        quizRepository.save(quiz)

        when:
        courseService.removeCourseExecution(courseExecutionTecnico.id)

        then: "the returned data are correct"
        thrown(TutorException)
        courseExecutionRepository.findAll().size() == existingCourseExecutions + 1
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
        courseExecutionRepository.findAll().size() == existingCourseExecutions + 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
