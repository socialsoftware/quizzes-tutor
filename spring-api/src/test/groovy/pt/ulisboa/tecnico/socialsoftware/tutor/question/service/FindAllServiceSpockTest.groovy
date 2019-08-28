package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ImageRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import spock.lang.Specification

@DataJpaTest
class FindAllServiceSpockTest extends Specification {
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String URL = 'URL'

    @Autowired
    QuestionService questionService

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    ImageRepository imageRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository


    def "create a question with image and two options and a quiz questions with two answers"() {
        given: "createQuestion a question"
        def question = new Question()
        question.setNumber(1)
        question.setContent(QUESTION_CONTENT)
        question.setActive(true)
        question.setNumberOfAnswers(0)
        question.setNumberOfCorrect(0)
        and: 'an image'
        def image = new Image()
        image.setUrl(URL)
        image.setWidth(20)
        imageRepository.save(image)
        question.setImage(image)
        and: 'two options'
        def optionOK = new Option()
        optionOK.setContent(OPTION_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setQuestion(question)
        optionRepository.save(optionOK)
        def options = new ArrayList<>()
        options.add(optionOK)
        def optionKO = new Option()
        optionKO.setContent(OPTION_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setQuestion(question)
        optionRepository.save(optionKO)
        options.add(optionKO)
        question.setOptions(options)
        questionRepository.save(question)
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


        when:
        def result = questionService.findAllQuestions(0, Integer.MAX_VALUE)

        then: "the returned data are correct"
        result.size() == 1
        def resQuestion = result.get(0)
        resQuestion.getId() != null
        resQuestion.getActive() == true
        resQuestion.getContent() == QUESTION_CONTENT
        resQuestion.getNumberOfAnswers() == 2
        resQuestion.getNumberOfCorrect() == 1
        resQuestion.getDifficulty() == 0.5
        resQuestion.getImage().getId() != null
        resQuestion.getImage().getUrl() == URL
        resQuestion.getImage().getWidth() == 20
        resQuestion.getOptions().size() == 2
    }



    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }

}
