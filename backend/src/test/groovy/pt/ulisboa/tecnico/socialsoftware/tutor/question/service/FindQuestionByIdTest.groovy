package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CodeFillInQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import spock.lang.Unroll

@DataJpaTest
class FindQuestionByIdTest extends SpockTest {

    def "simple multiple choice question lookup by id"() {
        given:
        Question question = new MultipleChoiceQuestion()
        question.setKey(1)
        question.setTitle("Question Title")
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        question.setCourse(course)
        questionRepository.save(question)

        when:
        def result = questionService.findQuestionById(question.getId())

        then:
        result instanceof MultipleChoiceQuestionDto
        result.getStatus() == Question.Status.AVAILABLE.name()
        result.getContent() == QUESTION_1_CONTENT
        result.getNumberOfAnswers() == 2
        result.getNumberOfCorrect() == 1
        result.getDifficulty() == 50
    }

    def "simple code fill in question lookup by id"() {
        given:
        Question question = new CodeFillInQuestion()
        question.setKey(1)
        question.setTitle("Question Title")
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        question.setCourse(course)
        questionRepository.save(question)

        when:
        def result = questionService.findQuestionById(question.getId())

        then:
        result instanceof CodeFillInQuestionDto
        result.getStatus() == Question.Status.AVAILABLE.name()
        result.getContent() == QUESTION_1_CONTENT
        result.getNumberOfAnswers() == 2
        result.getNumberOfCorrect() == 1
        result.getDifficulty() == 50
    }

    @Unroll
    def "lookup by id of non existent/valid ids"(Integer nonExistentId) {
        when:
        questionService.findQuestionById(nonExistentId)

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.QUESTION_NOT_FOUND

        where:
        nonExistentId << [-1, 0, 200]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}