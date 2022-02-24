package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question

@DataJpaTest
class UpdateDifficultQuestionTest extends SpockTest {
    def difficultQuestion
    def easyQuestion

    def setup() {
        createExternalCourseAndExecution()
    }

    def "update questions on access"() {
        given: "a difficult question"
        difficultQuestion = new Question()
        difficultQuestion.setKey(1)
        difficultQuestion.setTitle(QUESTION_1_TITLE)
        difficultQuestion.setContent(QUESTION_1_CONTENT)
        difficultQuestion.setStatus(Question.Status.AVAILABLE)
        difficultQuestion.setNumberOfAnswers(10)
        difficultQuestion.setNumberOfCorrect(1)
        difficultQuestion.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        difficultQuestion.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(difficultQuestion)

        def option = new Option()
        option.setContent(OPTION_1_CONTENT)
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestionDetails(questionDetails)
        optionRepository.save(option)

        and: "an easy question"
        easyQuestion = new Question()
        easyQuestion.setKey(2)
        easyQuestion.setTitle(QUESTION_2_TITLE)
        easyQuestion.setContent(QUESTION_2_CONTENT)
        easyQuestion.setStatus(Question.Status.AVAILABLE)
        easyQuestion.setNumberOfAnswers(2)
        easyQuestion.setNumberOfCorrect(1)
        easyQuestion.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        easyQuestion.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(easyQuestion)

        option = new Option()
        option.setContent(OPTION_2_CONTENT)
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestionDetails(questionDetails)
        optionRepository.save(option)

        when:
        difficultQuestionService.updateDifficultQuestions()

        then: "a difficult question is saved on the database"
        difficultQuestionRepository.count() == 1L

        and: "matches the difficult question"
        def result = difficultQuestionRepository.findAll().get(0)
        result.getId() == difficultQuestion.getId()
        result.getPercentage() ==  difficultQuestion.getDifficulty()
        !result.getRemoved()
    }

    def "update difficult question removed by student"() {
        given: "a difficult question removed by the student"
        def hardQuestion = new DifficultQuestion()
        hardQuestion.setPercentage(90)
        hardQuestion.setQuestion(difficultQuestion)
        hardQuestion.setRemoved(true)
        difficultQuestionRepository.save(hardQuestion)

        when:
        difficultQuestionService.weeklyRemoveReset()

        then: "the removed question is not removed anymore"
        def result = difficultQuestionRepository.findAll().get(0)
        !result.getRemoved()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
