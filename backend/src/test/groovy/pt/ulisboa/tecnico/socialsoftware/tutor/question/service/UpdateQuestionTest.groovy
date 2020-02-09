package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ImageRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_MISSING_DATA

@DataJpaTest
class UpdateQuestionTest extends Specification {
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String NEW_QUESTION_TITLE = 'new question title'
    public static final String NEW_QUESTION_CONTENT = 'new question content'
    public static final String NEW_OPTION_CONTENT = "new optionId content"
    public static final String URL = 'URL'

    @Autowired
    QuestionService questionService

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    ImageRepository imageRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    def question
    def optionOK
    def optionKO

    def setup() {
        given: "create a question"
        question = new Question()
        question.setKey(1)
        question.setContent(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        and: 'an image'
        def image = new Image()
        image.setUrl(URL)
        image.setWidth(20)
        imageRepository.save(image)
        question.setImage(image)
        and: 'two options'
        optionOK = new Option()
        optionOK.setContent(OPTION_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setQuestion(question)
        optionRepository.save(optionOK)
        optionKO = new Option()
        optionKO.setContent(OPTION_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setQuestion(question)
        optionRepository.save(optionKO)
        question.addOption(optionKO)
        question.addOption(optionOK)
        questionRepository.save(question)
    }

    def "update a question"() {
        given: "create a question"
        def questionDto = new QuestionDto()
        questionDto.setId(question.getId())
        questionDto.setTitle(NEW_QUESTION_TITLE)
        questionDto.setContent(NEW_QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setNumberOfAnswers(4)
        questionDto.setNumberOfCorrect(2)
        and: 'a optionId'
        def optionDto = new OptionDto()
        optionDto.setId(optionOK.getId())
        optionDto.setContent(NEW_OPTION_CONTENT)
        optionDto.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setId(optionKO.getId())
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.setOptions(options)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question is changed"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() == question.getId()
        result.getTitle() == NEW_QUESTION_TITLE
        result.getContent() == NEW_QUESTION_CONTENT
        and: 'are not changed'
        result.getStatus() == Question.Status.AVAILABLE
        result.getNumberOfAnswers() == 2
        result.getNumberOfCorrect() == 1
        result.getDifficulty() == 50
        result.getImage() != null
        and: 'an option is changed'
        result.getOptions().size() == 2
        def resOptionOne = result.getOptions().stream().filter({option -> option.getId() == optionOK.getId()}).findAny().orElse(null)
        resOptionOne.getContent() == NEW_OPTION_CONTENT
        !resOptionOne.getCorrect()
        def resOptionTwo = result.getOptions().stream().filter({option -> option.getId() == optionKO.getId()}).findAny().orElse(null)
        resOptionTwo.getContent() == OPTION_CONTENT
        resOptionTwo.getCorrect()
    }

    def "update question with missing data"() {
        given: 'a question'
        def questionDto = new QuestionDto()
        questionDto.setId(question.getId())
        questionDto.setTitle('     ')
        questionDto.setContent(NEW_QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.DISABLED.name())
        questionDto.setNumberOfAnswers(4)
        questionDto.setNumberOfCorrect(2)
        def optionDto = new OptionDto()
        optionDto.setId(optionOK.getId())
        optionDto.setContent(NEW_OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setId(optionKO.getId())
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.setOptions(options)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == QUESTION_MISSING_DATA
    }

    def "update question with two options true"() {
        given: 'a question'
        def questionDto = new QuestionDto()
        questionDto.setId(question.getId())
        questionDto.setTitle(NEW_QUESTION_TITLE)
        questionDto.setContent(NEW_QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.DISABLED.name())
        questionDto.setNumberOfAnswers(4)
        questionDto.setNumberOfCorrect(2)
        def optionDto = new OptionDto()
        optionDto.setId(optionOK.getId())
        optionDto.setContent(NEW_OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setId(optionKO.getId())
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.setOptions(options)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_MULTIPLE_CORRECT_OPTIONS
    }

    def "update correct option in a question with answers"() {
        given: "a question with answers"
        def quizQuestion = new QuizQuestion()
        quizQuestionRepository.save(quizQuestion)
        question.addQuizQuestion(quizQuestion)
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setOption(optionOK)
        questionAnswerRepository.save(questionAnswer)
        quizQuestion.addQuestionAnswer(questionAnswer)
        questionAnswer = new QuestionAnswer()
        questionAnswer.setOption(optionKO)
        questionAnswerRepository.save(questionAnswer)
        quizQuestion.addQuestionAnswer(questionAnswer)
        and: "createQuestion a question dto"
        def questionDto = new QuestionDto()
        questionDto.setId(question.getId())
        questionDto.setTitle(NEW_QUESTION_TITLE)
        questionDto.setContent(NEW_QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.DISABLED.name())
        questionDto.setNumberOfAnswers(4)
        questionDto.setNumberOfCorrect(2)
        and: 'a optionId'
        def optionDto = new OptionDto()
        optionDto.setId(optionOK.getId())
        optionDto.setContent(NEW_OPTION_CONTENT)
        optionDto.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setId(optionKO.getId())
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.setOptions(options)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_CHANGE_CORRECT_OPTION_HAS_ANSWERS
    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }

}
