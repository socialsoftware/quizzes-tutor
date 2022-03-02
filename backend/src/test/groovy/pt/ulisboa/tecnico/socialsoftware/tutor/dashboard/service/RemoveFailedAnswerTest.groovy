package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.FailedAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class RemoveFailedAnswerTest extends FailedAnswersSpockTest {

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)
    }

    def 'remove a failed answer' () {
        given:
        def quiz = createQuiz(1)
        def quizQuestion = createQuestion(1, quiz)
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz)
        def failedAnswer = createFailedAnswer(LocalDateTime.now(), false, questionAnswer)

        when:
        failedAnswerService.removeFailedAnswer(failedAnswer.getId())

        then: "the database contains a removed failed answer"
        failedAnswerRepository.findAll().size() == 1L
        def result = failedAnswerRepository.findAll().get(0)
        result.getId() != 0
        result.getAnswered()
        result.getCollected() != null
        result.getRemoved()
        result.getQuestionAnswer().id === questionAnswer.getId()
        result.getDashboard().id === dashboard.getId()
        result.getDashboard().getStudent().getId() === student.getId()

        and: "the dashboard does not contain the failed answer"
        def dashboard = dashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === student.getId()
        dashboard.getCourseExecution().getId() === externalCourseExecution.getId()
        dashboard.getFailedAnswers().findAll().size() == 0L
    }

    def 'remove one of two failed answers' () {
        given:
        def quiz1 = createQuiz(1)
        def quiz2 = createQuiz(2)
        def quizQuestion1 = createQuestion(1, quiz1)
        def questionAnswer1 = answerQuiz(true, false, true, quizQuestion1, quiz1)
        def quizQuestion2 = createQuestion(2, quiz2)
        def questionAnswer2 = answerQuiz(false, false, true, quizQuestion2, quiz2)
        def failedAnswer1 = createFailedAnswer(LocalDateTime.now(), false, questionAnswer1)
        def failedAnswer2 = createFailedAnswer(LocalDateTime.now(), false, questionAnswer2)

        when:
        failedAnswerService.removeFailedAnswer(failedAnswer1.getId())

        then: "the database contains one removed failed answer and another failed answer"
        failedAnswerRepository.findAll().size() == 2L
        def result1 = failedAnswerRepository.findAll().get(0)
        result1.getId() != 0
        result1.getAnswered()
        result1.getCollected() != null
        result1.getRemoved()
        result1.getQuestionAnswer().id === questionAnswer1.getId()
        result1.getDashboard().id === dashboard.getId()
        result1.getDashboard().getStudent().getId() === student.getId()
        def result2 = failedAnswerRepository.findAll().get(1)
        result2.getId() != 0
        !result2.getAnswered()
        result2.getCollected() != null
        !result2.getRemoved()
        result2.getQuestionAnswer().id === questionAnswer2.getId()
        result2.getDashboard().id === dashboard.getId()
        result2.getDashboard().getStudent().getId() === student.getId()

        and: "the dashboard only contains the second failed answer"
        def dashboard = dashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === student.getId()
        dashboard.getCourseExecution().getId() === externalCourseExecution.getId()
        dashboard.getFailedAnswers().findAll().size() == 1L
        def result = dashboard.getFailedAnswers().findAll().get(0)
        result.getId() != 0
        !result.getAnswered()
        result.getCollected() != null
        !result.getRemoved()
        result.getQuestionAnswer().id === questionAnswer2.getId()
    }

    @Unroll
    def "cannot remove failed answers with invalid failedAnswerId=#failedAnswerId" () {
        when:
        failedAnswerService.removeFailedAnswer(failedAnswerId)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        failedAnswerId  || errorMessage
        100             || ErrorMessage.FAILED_ANSWER_NOT_FOUND
        -1              || ErrorMessage.FAILED_ANSWER_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
