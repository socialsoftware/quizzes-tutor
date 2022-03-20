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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class GetFailedAnswersTest extends FailedAnswersSpockTest {
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
    def "get failed answer answered=#answered"() {
        given:
        def questionAnswer = answerQuiz(answered, false, true, quizQuestion, quiz)
        createFailedAnswer(questionAnswer, LocalDateTime.now())

        when:
        def failedAnswerDtos = failedAnswerService.getFailedAnswers(dashboard.getId())

        then: "the return statement contains one failed answer"
        failedAnswerDtos.size() == 1
        def failedAnswerDto = failedAnswerDtos.get(0)
        failedAnswerDto.getId() != 0
        failedAnswerDto.getAnswered() == answered
        LocalDateTime.parse(failedAnswerDto.getCollected(), DateTimeFormatter.ISO_DATE_TIME).isAfter(DateHandler.now().minusMinutes(1))
        failedAnswerDto.getQuestionAnswerDto().getQuestion().getId() === questionAnswer.getQuestion().getId()
        if (answered) failedAnswerDto.getQuestionAnswerDto().getAnswerDetails().getOption().getId() == questionAnswer.getAnswerDetails().getOption().getId()

        where:
        answered << [true, false]
    }

    def "get ordered failed answers"() {
        given:
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz)
        createFailedAnswer(questionAnswer, LocalDateTime.now())

        def quiz2 = createQuiz(2)
        def quizQuestion2 = createQuestion(2, quiz2)
        def questionAnswer2 = answerQuiz(true, false, true, quizQuestion2, quiz2)
        createFailedAnswer(questionAnswer2, LocalDateTime.now().plusDays(1))

        when:
        def failedAnswerDtos = failedAnswerService.getFailedAnswers(dashboard.getId())

        then: "the return statement contains two ordered failed answers"
        failedAnswerDtos.size() == 2
        def failedAnswer1 = failedAnswerDtos.get(0)
        failedAnswer1.getQuestionAnswerDto().getQuestion().getId() === questionAnswer2.getQuestion().getId()
        failedAnswer1.getQuestionAnswerDto().getAnswerDetails().getOption().getId() == questionAnswer2.getAnswerDetails().getOption().getId()
        def failedAnswer2 = failedAnswerDtos.get(1)
        failedAnswer2.getQuestionAnswerDto().getQuestion().getId() === questionAnswer.getQuestion().getId()
        failedAnswer2.getQuestionAnswerDto().getAnswerDetails().getOption().getId() == questionAnswer.getAnswerDetails().getOption().getId()
    }

    @Unroll
    def "cannot get failed answer with dashboardId=#dashboardId"() {
        given:
        answerQuiz(true, false, true, quizQuestion, quiz)

        when:
        failedAnswerService.getFailedAnswers(dashboardId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DASHBOARD_NOT_FOUND

        where:
        dashboardId << [0, 100]
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}