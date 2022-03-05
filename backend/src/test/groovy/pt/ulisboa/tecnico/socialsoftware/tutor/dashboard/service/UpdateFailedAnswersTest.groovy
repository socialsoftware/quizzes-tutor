package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class UpdateFailedAnswersTest extends FailedAnswersSpockTest {
    def quiz
    def quizQuestion

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)

        quiz = createQuiz(1)
        quizQuestion = createQuestion(1, quiz)
    }

    @Unroll
    def "update failed answer answered=#answered"() {
        given:
        def questionAnswer = answerQuiz(answered, false, true, quizQuestion, quiz)

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId())

        then: "a failed answer is saved in the database"
        failedAnswerRepository.count() == 1L
        def failedAnswer = failedAnswerRepository.findAll().get(0)
        failedAnswer.getId() != 0
        failedAnswer.getDashboard().id === dashboard.getId()
        failedAnswer.getQuestionAnswer().getId() == questionAnswer.getId()
        failedAnswer.getCollected().isAfter(DateHandler.now().minusMinutes(1))
        failedAnswer.getAnswered() == answered

        and: "is updated in the student dashboard"
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getFailedAnswers().contains(failedAnswer)

        where:
        answered << [true, false]
    }

    def "updates failed answers after last check"() {
        given:
        dashboard.setLastCheckFailedAnswers(LocalDateTime.now().minusDays(1))
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz, LocalDateTime.now().minusDays(2))
        createFailedAnswer(questionAnswer, LocalDateTime.now().minusDays(2))

        def quiz2 = createQuiz(2)
        def quizQuestion2 = createQuestion(2, quiz2)
        def questionAnswer2 = answerQuiz(true, false, true, quizQuestion2, quiz2)

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId())

        then: "both failed answers are saved in the database"
        failedAnswerRepository.count() == 2L
        def failedAnswer = failedAnswerRepository.findAll().get(0)
        failedAnswer.getQuestionAnswer().getId() == questionAnswer.getId()
        def failedAnswer2 = failedAnswerRepository.findAll().get(1)
        failedAnswer2.getQuestionAnswer().getId() == questionAnswer2.getId()

        and: "the updated failed answer is in the student dashboard"
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getFailedAnswers().size() == 2
        dashboard.getFailedAnswers().contains(failedAnswer)
        dashboard.getFailedAnswers().contains(failedAnswer2)
    }

    def 'empty failed answers with correct=#correct and completed=#completed"' () {
        given:
        answerQuiz(true, correct, completed, quizQuestion, quiz)

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId())

        then: "no failed answer is updated in the database"
        failedAnswerRepository.findAll().size() == 0L

        and: "the student dashboard's failed answers is empty"
        def dashboard = dashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === student.getId()
        dashboard.getCourseExecution().getId() === externalCourseExecution.getId()
        dashboard.getFailedAnswers().findAll().size() == 0L

        where:
        completed | correct
        false     | false
        true      | true
    }

    def "empty failed answers if not answered by the student"() {
        given:
        def otherStudent = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        otherStudent.addCourse(externalCourseExecution)
        userRepository.save(otherStudent)
        and:
        dashboard.setStudent(otherStudent)
        and:
        answerQuiz(true, false, true, quizQuestion, quiz)

        when:
        failedAnswerService.getFailedAnswers(dashboard.getId())

        then: "no failed answer is updated in the database"
        failedAnswerRepository.findAll().size() == 0L

        and: "the student dashboard's failed answers is empty"
        def dashboard = dashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === otherStudent.getId()
        dashboard.getCourseExecution().getId() === externalCourseExecution.getId()
        dashboard.getFailedAnswers().findAll().size() == 0L
    }

    def "empty failed answers if quiz does not belong to the course execution"() {
        given:
        def otherExternalCourseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(otherExternalCourseExecution)
        and:
        dashboard.setCourseExecution(otherExternalCourseExecution)
        and:
        answerQuiz(true, false, true, quizQuestion, quiz)

        when:
        failedAnswerService.getFailedAnswers(dashboard.getId())

        then: "no failed answer is updated in the database"
        failedAnswerRepository.findAll().size() == 0L

        and: "the student dashboard's failed answers is empty"
        def dashboard = dashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === student.getId()
        dashboard.getCourseExecution().getId() === otherExternalCourseExecution.getId()
        dashboard.getFailedAnswers().findAll().size() == 0L
    }

    @Unroll
    def "cannot update failed answers with dashboardId=#dashboardId"() {
        when:
        failedAnswerService.updateFailedAnswers(dashboardId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DASHBOARD_NOT_FOUND
        and:
        failedAnswerRepository.count() == 0L

        where:
        dashboardId << [0, 100]
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
