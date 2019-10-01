package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import spock.lang.Specification

@DataJpaTest
class UpdateTopicsServiceSpockTest extends Specification {
    public static final String TOPIC_ONE = 'nameOne'
    public static final String TOPIC_TWO = 'nameTwo'
    public static final String TOPIC_THREE = 'nameThree'

    @Autowired
    QuestionService questionService

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    TopicRepository topicRepository

    def question
    def topicDtoOne
    def topicDtoTwo
    def topicDtoThree
    def topicOne
    def topicTwo
    def topicThree

    def setup() {
        question = new Question()
        question.setNumber(1)

        topicDtoOne = new TopicDto()
        topicDtoOne.setName(TOPIC_ONE)
        topicDtoTwo = new TopicDto()
        topicDtoTwo.setName(TOPIC_TWO)
        topicDtoThree = new TopicDto()
        topicDtoThree.setName(TOPIC_THREE)

        topicOne = new Topic(topicDtoOne)
        topicTwo = new Topic(topicDtoTwo)
        question.getTopics().add(topicOne)
        topicOne.getQuestions().add(question)
        question.getTopics().add(topicTwo)
        topicTwo.getQuestions().add(question)
        questionRepository.save(question)
        topicRepository.save(topicOne)
        topicRepository.save(topicTwo)

        topicThree = new Topic(topicDtoThree)
        topicRepository.save(topicThree)
    }

    def "add one topic"() {
        given:
        TopicDto[] topics = [topicDtoOne, topicDtoTwo, topicDtoThree]

        when:
        questionService.updateQuestionTopics(question.getId(), topics)

        then:
        question.getTopics().size() == 3
        question.getTopics().contains(topicOne)
        question.getTopics().contains(topicTwo)
        question.getTopics().contains(topicThree)
        topicOne.getQuestions().size() == 1
        topicTwo.getQuestions().size() == 1
        topicThree.getQuestions().size() == 1
    }

    def "remove one topic"() {
        given:
        TopicDto[] topics = [topicDtoOne]

        when:
        questionService.updateQuestionTopics(question.getId(), topics)

        then:
        question.getTopics().size() == 1
        question.getTopics().contains(topicOne)
        topicOne.getQuestions().size() == 1
        topicTwo.getQuestions().size() == 0
        topicThree.getQuestions().size() == 0
    }

    def "doesn not change"() {
        given:
        TopicDto[] topics = [topicDtoOne, topicDtoTwo]

        when:
        questionService.updateQuestionTopics(question.getId(), topics)

        then:
        question.getTopics().size() == 2
        question.getTopics().contains(topicOne)
        question.getTopics().contains(topicTwo)
        topicOne.getQuestions().size() == 1
        topicTwo.getQuestions().size() == 1
        topicThree.getQuestions().size() == 0
    }

    def "add one topic, maintain one topic, remove one topic"() {
        given:
        TopicDto[] topics = [topicDtoOne, topicDtoThree]

        when:
        questionService.updateQuestionTopics(question.getId(), topics)

        then:
        question.getTopics().size() == 2
        question.getTopics().contains(topicOne)
        question.getTopics().contains(topicThree)
        topicOne.getQuestions().size() == 1
        topicTwo.getQuestions().size() == 0
        topicThree.getQuestions().size() == 1
    }


    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }

}
