package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import spock.lang.Specification

@DataJpaTest
class RemoveTopicServiceSpockTest extends Specification {
    private static final String TOPIC_ONE = 'nameOne'
    private static final String TOPIC_TWO = 'nameTwo'
    private static final String TOPIC_THREE = 'nameThree'
    private static final Integer NUMBER = 1

    @Autowired
    TopicService topicService

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    TopicRepository topicRepository

    def question
    def topicOne
    def topicTwo
    def topicThree

    def setup() {
        question = new Question()
        question.setNumber(NUMBER)
        topicOne = new Topic(TOPIC_ONE)
        topicTwo = new Topic(TOPIC_TWO)
        question.getTopics().add(topicOne)
        topicOne.getQuestions().add(question)
        question.getTopics().add(topicTwo)
        topicTwo.getQuestions().add(question)
        questionRepository.save(question)
        topicRepository.save(topicOne)
        topicRepository.save(topicTwo)

        topicThree = new Topic(TOPIC_THREE)
        topicRepository.save(topicThree)
    }

    def "remove topic"() {
        when:
        topicService.removeTopic(TOPIC_ONE)

        then:
        topicOne.getQuestions().size() == 0
        topicTwo.getQuestions().size() == 1
        question.getTopics().size() == 1
        question.getTopics().contains(topicTwo)
    }

    def "remove topic has not question"() {
        when:
        topicService.removeTopic(TOPIC_THREE)

        then:
        topicRepository.findAll().size() == 2
        topicOne.getQuestions().size() == 1
        topicTwo.getQuestions().size() == 1
        question.getTopics().size() == 2
        question.getTopics().contains(topicOne)
        question.getTopics().contains(topicTwo)
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
