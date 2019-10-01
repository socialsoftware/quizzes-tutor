package pt.ulisboa.tecnico.socialsoftware.tutor.impexp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import spock.lang.Specification

@DataJpaTest
class ImportExportTopicsSpockTest extends Specification {
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String TOPIC_ONE = "topicOne"
    public static final String TOPIC_TWO = "topicTwo"

    @Autowired
    QuestionService questionService

    @Autowired
    TopicService topicService

    @Autowired
    TopicRepository topicRepository

    def topicDtoOne
    def topicDtoTwo

    def setup() {
        def question = new QuestionDto()
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setActive(true)

        def option = new OptionDto()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(option)
        question.setOptions(options)

        question = questionService.createQuestion(question)

        topicDtoOne = new TopicDto()
        topicDtoOne.setName(TOPIC_ONE)
        topicDtoTwo = new TopicDto()
        topicDtoTwo.setName(TOPIC_TWO)

        topicDtoOne = topicService.createTopic(topicDtoOne)
        topicDtoTwo = topicService.createTopic(topicDtoTwo)

        TopicDto[] topics = [topicDtoOne, topicDtoTwo]
        questionService.updateQuestionTopics(question.getId(), topics)
    }

    def 'export and import topics'() {
        given: 'a xml with questions'
        def topicsXml = topicService.exportTopics()
        and: 'delete topics'
        topicService.removeTopic(topicDtoOne.getId())
        topicService.removeTopic(topicDtoTwo.getId())

        when:
        topicService.importTopics(topicsXml)

        then:
        topicRepository.findAll().size() == 2
        def topicOne = topicRepository.findAll().get(0)
        def topicTwo = topicRepository.findAll().get(1)
        topicOne.getName() == TOPIC_ONE && topicTwo.getName() == TOPIC_TWO ||
                topicOne.getName() == TOPIC_TWO && topicTwo.getName() == TOPIC_ONE

        topicOne.getQuestions().size() == 1
        topicTwo.getQuestions().size() == 1
        topicOne.getQuestions() == topicTwo.getQuestions()

        def question = topicOne.getQuestions().stream().findAny().orElse(null)
        question.getTopics().size() == 2
        question.getTopics().stream().map{topic -> topic.getName()}
                .allMatch{topic -> topic == TOPIC_ONE || topic == TOPIC_TWO}
    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {
        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        TopicService topicService() {
            return new TopicService()
        }
    }
}
