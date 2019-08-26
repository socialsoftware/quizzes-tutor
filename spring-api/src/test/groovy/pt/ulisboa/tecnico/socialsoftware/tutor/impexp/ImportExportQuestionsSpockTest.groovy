package pt.ulisboa.tecnico.socialsoftware.tutor.impexp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.service.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.service.UserService
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
        def question = new QuestionDto()
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setActive(true)

        def image = new ImageDto()
        image.setUrl(URL)
        image.setWidth(20)
        question.setImage(image)

        def option = new OptionDto()
        option.setNumber(0)
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(option)
        option = new OptionDto()
        option.setNumber(1)
        option.setContent(OPTION_CONTENT)
        option.setCorrect(false)
        options.add(option)
        question.setOptions(options)

        questionService.createQuestion(question)
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
        questionResult.getActive()
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
