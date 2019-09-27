package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import spock.lang.Specification

@DataJpaTest
class CreateTopicServiceSpockTest extends Specification {
    public static final String NAME = 'name'

    @Autowired
    QuestionService questionService

    @Autowired
    TopicRepository topicRepository

    def "create a topic"() {
        when:
        questionService.createTopic(NAME)

        then: "the topic is inside the repository"
        topicRepository.count() == 1L
        def result = topicRepository.findAll().get(0)
        result.getId() != null
        result.getName() == NAME
    }

    def "create a topic with the same name"() {
        given: "createQuestion a question"
        Topic topic = new Topic()
        topic.setName(NAME)
        topicRepository.save(topic)

        when: 'createQuestion another with the same name'
        questionService.createTopic(NAME)

        then: "an error occurs"
        def exception = thrown(TutorException)
        exception.error == ExceptionError.DUPLICATE_TOPIC
    }


    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }

}
