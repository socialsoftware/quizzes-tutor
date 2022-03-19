package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DifficultQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CANNOT_REMOVE_DIFFICULT_QUESTION
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DIFFICULT_QUESTION_NOT_FOUND

@DataJpaTest
class RemoveDifficultQuestionTest extends SpockTest {
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

    def "student removes a difficult question"() {
        given:
        def difficultQuestion = new DifficultQuestion(dashboard, question, 24)
        difficultQuestionRepository.save(difficultQuestion)

        when:
        difficultQuestionService.removeDifficultQuestion(difficultQuestion.getId())

        then:
        difficultQuestionRepository.count() == 1
        and:
        def result = difficultQuestionRepository.findAll().get(0)
        result.getId() == difficultQuestion.getId()
        result.isRemoved() == true
        result.getRemovedDate().isAfter(DateHandler.now().minusSeconds(30))
    }

    def "cannot remove a removed difficult question"() {
        given:
        def now = DateHandler.now()
        and:
        def difficultQuestion = new DifficultQuestion(dashboard, question, 24)
        difficultQuestion.setRemoved(true)
        difficultQuestion.setRemovedDate(now)
        difficultQuestionRepository.save(difficultQuestion)

        when:
        difficultQuestionService.removeDifficultQuestion(difficultQuestion.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == CANNOT_REMOVE_DIFFICULT_QUESTION
        and:
        difficultQuestionRepository.count() == 1
        and:
        def result = difficultQuestionRepository.findAll().get(0)
        result.isRemoved() == true
        result.getRemovedDate().equals(now)
    }

    @Unroll
    def "the difficult question cannot be deleted because invalid difficultQuestionId #id"() {
        when:
        difficultQuestionService.removeDifficultQuestion(id)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == DIFFICULT_QUESTION_NOT_FOUND

        where:
        id  << [0, 100]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}