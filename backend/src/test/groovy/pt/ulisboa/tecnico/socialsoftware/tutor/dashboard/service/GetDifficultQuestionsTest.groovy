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
        given: "a difficult question"
        def difficultQuestion = new DifficultQuestion(dashboard, question, 24)
        difficultQuestion.setRemovedDate(DateHandler.now().minusDays(daysSince))
        difficultQuestion.setRemoved(true)
        difficultQuestionRepository.save(difficultQuestion)

        when:
        List<DifficultQuestionDto> difficultQuestions = difficultQuestionService.getDifficultQuestions(dashboard.getId())

        then: "a single difficult question is returned"
        difficultQuestions.size() == 1

        and: "matches the created difficult question"
        DifficultQuestionDto resultDifficultQuestion = difficultQuestions.get(0)
        resultDifficultQuestion.getId() == difficultQuestion.getId()
        resultDifficultQuestion.getPercentage() == difficultQuestion.getPercentage()
    }

    def "get multiple difficult questions"() {
        given: "a second question"
        questionB = new Question()
        questionB.setKey(2)
        questionB.setTitle(QUESTION_1_TITLE)
        questionB.setContent(QUESTION_1_CONTENT)
        questionB.setStatus(Question.Status.AVAILABLE)
        questionB.setNumberOfAnswers(2)
        questionB.setNumberOfCorrect(1)
        questionB.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(questionB)

        def optionC = new Option()
        optionC.setContent(OPTION_1_CONTENT)
        optionC.setCorrect(true)
        optionC.setSequence(0)
        optionC.setQuestionDetails(questionDetails)
        optionRepository.save(optionC)

        def optionD = new Option()
        optionD.setContent(OPTION_1_CONTENT)
        optionD.setCorrect(false)
        optionD.setSequence(1)
        optionD.setQuestionDetails(questionDetails)
        optionRepository.save(optionD)

        and: "two different difficult questions"
        def difficultQuestionA = new DifficultQuestion(dashboard, question, 24)
        difficultQuestionA.setRemovedDate(DateHandler.now().minusDays(daysSince))
        difficultQuestionA.setRemoved(true)
        difficultQuestionRepository.save(difficultQuestionA)

        def difficultQuestionB = new DifficultQuestion(dashboard, questionB, 24)
        difficultQuestionB.setRemovedDate(DateHandler.now().minusDays(daysSince))
        difficultQuestionB.setRemoved(true)
        difficultQuestionRepository.save(difficultQuestionB)

        when:
        List<DifficultQuestionDto> difficultQuestions = difficultQuestionService.getDifficultQuestions(dashboard.getId())

        then: "two difficult questions are returned"
        difficultQuestions.size() == 2
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