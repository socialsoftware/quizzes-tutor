package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll

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

    @Unroll
    def 'remove a failed answer minusDays #minusDays' () {
        given:
        def quiz = createQuiz(1)
        def quizQuestion = createQuestion(1, quiz)
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz)
        def failedAnswer = createFailedAnswer(questionAnswer, DateHandler.now().minusDays(minusDays))

        when:
        failedAnswerService.removeFailedAnswer(failedAnswer.getId())

        then:
        failedAnswerRepository.findAll().size() == 0L
        and:
        def dashboard = dashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === student.getId()
        dashboard.getCourseExecution().getId() === externalCourseExecution.getId()
        dashboard.getFailedAnswers().findAll().size() == 0L

        where:
        minusDays << [8, 5]
    }

    def 'remove a failed answer when there is another with the same question' () {
        given:
        def quiz = createQuiz(1)
        def quizQuestion = createQuestion(1, quiz)
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz)
        def failedAnswer = createFailedAnswer(questionAnswer, DateHandler.now().minusDays(5))
        and:
        def quiz2 = createQuiz(2)
        def quizQuestion2 = addExistingQuestionToQuiz(quiz2)
        def questionAnswer2 = answerQuiz(true, false, true, quizQuestion2, quiz2)
        def failedAnswer2 = createFailedAnswer(questionAnswer2, DateHandler.now().minusDays(5))

        when:
        failedAnswerService.removeFailedAnswer(failedAnswer2.getId())

        then:
        failedAnswerRepository.findAll().size() == 1L
        and:
        def dashboard = dashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === student.getId()
        dashboard.getCourseExecution().getId() === externalCourseExecution.getId()
        dashboard.getFailedAnswers().size() == 1
        dashboard.getFailedAnswers().contains(failedAnswer)
    }

    def 'remove a failed answer when there is two others with the same question' () {
        given:
        def quiz = createQuiz(1)
        def quizQuestion = createQuestion(1, quiz)
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz)
        def failedAnswer = createFailedAnswer(questionAnswer, DateHandler.now().minusDays(5))
        and:
        def quiz2 = createQuiz(2)
        def quizQuestion2 = addExistingQuestionToQuiz(quiz2)
        def questionAnswer2 = answerQuiz(true, false, true, quizQuestion2, quiz2)
        def failedAnswer2 = createFailedAnswer(questionAnswer2, DateHandler.now().minusDays(5))
        and:
        def quiz3 = createQuiz(3)
        def quizQuestion3 = addExistingQuestionToQuiz(quiz3)
        def questionAnswer3 = answerQuiz(true, false, true, quizQuestion3, quiz3)
        def failedAnswer3 = createFailedAnswer(questionAnswer3, DateHandler.now().minusDays(5))

        when:
        failedAnswerService.removeFailedAnswer(failedAnswer2.getId())

        then:
        failedAnswerRepository.findAll().size() == 2L
        and:
        def dashboard = dashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === student.getId()
        dashboard.getCourseExecution().getId() === externalCourseExecution.getId()
        dashboard.getFailedAnswers().size() == 2
        dashboard.getFailedAnswers().contains(failedAnswer)
        dashboard.getFailedAnswers().contains(failedAnswer3)
    }

    @Unroll
    def 'cannot remove a failed answer minusDays #minusDays' () {
        given:
        def quiz = createQuiz(1)
        def quizQuestion = createQuestion(1, quiz)
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz)
        def failedAnswer = createFailedAnswer(questionAnswer, DateHandler.now().minusDays(minusDays))

        when:
        failedAnswerService.removeFailedAnswer(failedAnswer.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CANNOT_REMOVE_FAILED_ANSWER
        and:
        failedAnswerRepository.findAll().size() == 1L

        where:
        minusDays << [0, 4]
    }

    @Unroll
    def "cannot remove failed answers with invalid failedAnswerId=#failedAnswerId" () {
        when:
        failedAnswerService.removeFailedAnswer(failedAnswerId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.FAILED_ANSWER_NOT_FOUND

        where:
        failedAnswerId << [-1, 100]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}