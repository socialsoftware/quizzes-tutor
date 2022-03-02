package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class ReAddFailedAnswersTest extends FailedAnswersSpockTest {

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)
    }

    def 're-add a removed failed answer from a valid time period' () {
        given:
        def quiz = createQuiz(1)
        def quizQuestion = createQuestion(1, quiz)
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz)
        createFailedAnswer(LocalDateTime.now(), true, questionAnswer)

        when:
        failedAnswerService.reAddFailedAnswers(dashboard.getId(), STRING_DATE_YESTERDAY, STRING_DATE_LATER)

        then: "the database contains a re-added failed answer"
        failedAnswerRepository.findAll().size() == 1L
        def result = failedAnswerRepository.findAll().get(0)
        result.getId() != 0
        result.getAnswered()
        result.getCollected() != null
        !result.getRemoved()
        result.getQuestionAnswer().id === questionAnswer.getId()
        result.getDashboard().id === dashboard.getId()
        result.getDashboard().getStudent().getId() === student.getId()
        and: "the dashboard now contains the failed answer"
        def dashboard = dashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === student.getId()
        dashboard.getCourseExecution().getId() === externalCourseExecution.getId()
        dashboard.getFailedAnswers().findAll().size() == 1L
        def result1 = dashboard.getFailedAnswers().findAll().get(0)
        result1.getId() != 0
        result1.getAnswered()
        result1.getCollected() != null
        !result1.getRemoved()
        result1.getQuestionAnswer().id === questionAnswer.getId()
    }

    def 're-adds failed answers from other time period' () {
        given:
        def quiz = createQuiz(1)
        def quizQuestion = createQuestion(1, quiz)
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz)
        createFailedAnswer(LocalDateTime.now(), true, questionAnswer)

        when:
        failedAnswerService.reAddFailedAnswers(dashboard.getId(), STRING_DATE_TOMORROW, STRING_DATE_LATER)

        then: "the database still contains a removed failed answer"
        failedAnswerRepository.findAll().size() == 1L
        def result = failedAnswerRepository.findAll().get(0)
        result.getId() != 0
        result.getAnswered()
        result.getCollected() != null
        result.getRemoved()
        result.getQuestionAnswer().id === questionAnswer.getId()
        result.getDashboard().id === dashboard.getId()
        result.getDashboard().getStudent().getId() === student.getId()
        and: "the dashboard does not contains the failed answer"
        def dashboard = dashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === student.getId()
        dashboard.getCourseExecution().getId() === externalCourseExecution.getId()
        dashboard.getFailedAnswers().findAll().size() == 0L
    }

    @Unroll
    def "cannot re-add failed answers for invalid dashboard (#dashboardId) | invalid period (#startDate, #endDate) "() {
        when:
        failedAnswerService.reAddFailedAnswers(dashboardId, startDate, endDate)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        dashboardId   | startDate             | endDate               || errorMessage
        -1            | STRING_DATE_YESTERDAY | STRING_DATE_LATER     || ErrorMessage.DASHBOARD_NOT_FOUND
        1             | null                  | STRING_DATE_LATER     || ErrorMessage.FAILED_ANSWER_MISSING_START_TIME
        1             | STRING_DATE_YESTERDAY | null                  || ErrorMessage.FAILED_ANSWER_MISSING_END_TIME
        1             | STRING_DATE_LATER     | STRING_DATE_YESTERDAY || ErrorMessage.INVALID_DATE_INTERVAL
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
