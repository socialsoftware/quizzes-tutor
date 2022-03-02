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
class GetFilteredFailedAnswersTest extends FailedAnswersSpockTest {

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)
    }


    def 'get filtered failed answers' () {
        given:
        def quiz1 = createQuiz(1)
        def quizQuestion1 = createQuestion(1, quiz1)
        def questionAnswer1 = answerQuiz(true, false, true, quizQuestion1, quiz1)
        def quiz2 = createQuiz(2)
        def quizQuestion2 = createQuestion(2, quiz2)
        def questionAnswer2 = answerQuiz(false, false, true, quizQuestion2, quiz2)
        createFailedAnswer(LocalDateTime.now(), false, questionAnswer1)
        createFailedAnswer(LocalDateTime.now(), false, questionAnswer2)

        when:
        def result = failedAnswerService.getFilteredFailedAnswers(dashboard.getId(), STRING_DATE_YESTERDAY, STRING_DATE_LATER)

        then: "the return statement contains two failed answers"
        result.size() == 2
        def result1 = result.get(0)
        result1.getId() != 0
        result1.getAnswered()
        result1.getCollected() != null
        !result1.getRemoved()
        result1.getQuestionAnswer().id === questionAnswer1.getId()
        def result2 = result.get(1)
        result2.getId() != 0
        !result2.getAnswered()
        result2.getCollected() != null
        !result2.getRemoved()
        result2.getQuestionAnswer().id === questionAnswer2.getId()
    }

    def 'get some filtered failed answers' () {
        given:
        def quiz1 = createQuiz(1)
        def quizQuestion1 = createQuestion(1, quiz1)
        def questionAnswer1 = answerQuiz(true, false, true, quizQuestion1, quiz1)
        def quiz2 = createQuiz(2)
        def quizQuestion2 = createQuestion(2, quiz2)
        def questionAnswer2 = answerQuiz(false, false, true, quizQuestion2, quiz2)

        createFailedAnswer(LocalDateTime.now(), false, questionAnswer1)
        createFailedAnswer(LocalDateTime.now().minusDays(2), false, questionAnswer2)

        when:
        def result = failedAnswerService.getFilteredFailedAnswers(dashboard.getId(), STRING_DATE_YESTERDAY, STRING_DATE_LATER)

        then: "the return statement only contains one failed answer"
        result.size() == 1
        def result1 = result.get(0)
        result1.getId() != 0
        result1.getAnswered()
        result1.getCollected() != null
        !result1.getRemoved()
        result1.getQuestionAnswer().id === questionAnswer1.getId()
    }

    def 'get empty filtered failed answers' () {
        given:
        def quiz1 = createQuiz(1)
        def quizQuestion1 = createQuestion(1, quiz1)
        def questionAnswer1 = answerQuiz(true, false, true, quizQuestion1, quiz1)
        def quiz2 = createQuiz(2)
        def quizQuestion2 = createQuestion(2, quiz2)
        def questionAnswer2 = answerQuiz(false, false, true, quizQuestion2, quiz2)

        createFailedAnswer(LocalDateTime.now().minusDays(2), false, questionAnswer1)
        createFailedAnswer(LocalDateTime.now().minusDays(2), false, questionAnswer2)

        when:
        def result = failedAnswerService.getFilteredFailedAnswers(dashboard.getId(), STRING_DATE_YESTERDAY, STRING_DATE_LATER)

        then: "the return statement does not contain failed answers"
        result.size() == 0
    }

    @Unroll
    def "cannot get filtered failed answers for invalid dashboard (#dashboardId) | invalid period (#startDate, #endDate) "() {
    when:
        failedAnswerService.getFilteredFailedAnswers(dashboardId, startDate, endDate)

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
