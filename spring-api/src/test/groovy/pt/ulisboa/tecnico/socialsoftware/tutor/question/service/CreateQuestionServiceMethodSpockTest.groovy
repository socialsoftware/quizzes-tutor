package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ImageRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.service.QuestionService
import spock.lang.Specification

@DataJpaTest
class CreateQuestionServiceMethodSpockTest extends Specification {
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "option content"
    public static final String URL = 'URL'

    @Autowired
    QuestionService questionService

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    ImageRepository imageRepository

    def "create a question with no image and one option"() {
        given: "create a question"
        def question = new QuestionDto()
        question.setContent(QUESTION_CONTENT)
        question.setActive(true)
        question.setDifficulty(1)
        def option = new OptionDto()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        def options = new ArrayList<>()
        options.add(option)
        question.setOptions(options)

        when:
        questionService.create(question)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getActive() == true
        result.getContent() == QUESTION_CONTENT
        result.getDifficulty() == 1
        result.getImage() == null
        result.getOptions().size() == 1
        def resOption = result.getOptions().get(0)
        resOption.getContent() == OPTION_CONTENT
        resOption.getCorrect() == true

    }


    def "create a question with image and two options"() {
        given: "create a question"
        def question = new QuestionDto()
        question.setContent(QUESTION_CONTENT)
        question.setActive(true)
        question.setDifficulty(1)
        def image = new ImageDto()
        image.setUrl(URL)
        image.setWidth(20)
        question.setImage(image)
        def option = new OptionDto()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        def options = new ArrayList<>()
        options.add(option)
        option = new OptionDto()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        options.add(option)
        question.setOptions(options)

        when:
        questionService.create(question)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getActive() == true
        result.getContent() == QUESTION_CONTENT
        result.getDifficulty() == 1
        result.getImage().getId() != null
        result.getImage().getUrl() == URL
        result.getImage().getWidth() == 20
        result.getOptions().size() == 2
    }


    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }

}
