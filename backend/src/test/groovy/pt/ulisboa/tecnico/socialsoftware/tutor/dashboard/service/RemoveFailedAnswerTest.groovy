package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
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
    def 'remove a failed answer minusDays #minusDays'() {
        given:
        def question = createQuestion()
        def quiz = createQuiz()
        def quizQuestion = createQuizQuestion(quiz, question)
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

    @Unroll
    def "cannot remove failed answers with invalid failedAnswerId=#failedAnswerId"() {
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