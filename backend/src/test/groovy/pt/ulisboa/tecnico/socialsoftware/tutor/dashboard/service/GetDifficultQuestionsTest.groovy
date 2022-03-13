package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DifficultQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.WeeklyScore
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DASHBOARD_NOT_FOUND

@DataJpaTest
class GetDifficultQuestionsTest extends SpockTest {
    def student
    def dashboard
    def question
    def optionOK
    def optionKO

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        question.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)

        optionKO = new Option()
        optionKO.setContent(OPTION_1_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)
    }

    def "get difficult question"() {
        given:
        def difficultQuestion = new DifficultQuestion(dashboard, question, 24)
        difficultQuestionRepository.save(difficultQuestion)

        when:
        def difficultQuestions = difficultQuestionService.getDifficultQuestions(dashboard.getId())

        then:
        difficultQuestions.size() == 1
        and:
        def resultDifficultQuestion = difficultQuestions.get(0)
        resultDifficultQuestion.getId() == difficultQuestion.getId()
        resultDifficultQuestion.getPercentage() == 24
        resultDifficultQuestion.getRemovedDate() == null
        !resultDifficultQuestion.isRemoved()
        resultDifficultQuestion.getQuestionDto().getId() == question.getId()
    }

    def "does not get removed difficult questions"() {
        given:
        def difficultQuestion = new DifficultQuestion(dashboard, question, 24)
        difficultQuestion.setRemovedDate(DateHandler.now().minusDays(3))
        difficultQuestion.setRemoved(true)
        difficultQuestionRepository.save(difficultQuestion)

        when:
        def difficultQuestions = difficultQuestionService.getDifficultQuestions(dashboard.getId())

        then:
        difficultQuestions.size() == 0
    }

    @Unroll
    def "cannot get difficult questions because invalid dashboard #id"() {
        when:
        difficultQuestionService.getDifficultQuestions(id)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        id  || errorMessage
        0   || DASHBOARD_NOT_FOUND
        100 || DASHBOARD_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}