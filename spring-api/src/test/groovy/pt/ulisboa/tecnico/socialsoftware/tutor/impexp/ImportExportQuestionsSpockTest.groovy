package pt.ulisboa.tecnico.socialsoftware.tutor.impexp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import spock.lang.Specification

@DataJpaTest
class ImportExportQuestionsSpockTest extends Specification {
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String URL = 'URL'

    @Autowired
    QuestionService questionService

    @Autowired
    QuestionRepository questionRepository

    def setup() {
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def image = new ImageDto()
        image.setUrl(URL)
        image.setWidth(20)
        questionDto.setImage(image)

        def optionDto = new OptionDto()
        optionDto.setNumber(0)
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setNumber(1)
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.setOptions(options)

        questionService.createQuestion(questionDto)
    }

    def 'export and import questions'() {
        given: 'a xml with questions'
        def questionsXml = questionService.exportQuestions()
        and: 'a clean database'
        questionRepository.deleteAll()

        when:
        questionService.importQuestions(questionsXml)

        then:
        questionRepository.findAll().size() == 1
        def questionResult = questionRepository.findAll().get(0)
        questionResult.getTitle() == QUESTION_TITLE
        questionResult.getContent() == QUESTION_CONTENT
        questionResult.getStatus() == Question.Status.AVAILABLE
        def imageResult = questionResult.getImage()
        imageResult.getWidth() == 20
        imageResult.getUrl() == URL
        questionResult.getOptions().size() == 2
        def optionOneResult = questionResult.getOptions().get(0)
        def optionTwoResult = questionResult.getOptions().get(1)
        optionOneResult.getNumber() + optionTwoResult.getNumber() == 1
        optionOneResult.getContent() == OPTION_CONTENT
        optionTwoResult.getContent() == OPTION_CONTENT
        !(optionOneResult.getCorrect() && optionTwoResult.getCorrect())
        optionOneResult.getCorrect() || optionTwoResult.getCorrect()
    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }

}
