package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto

@DataJpaTest
class CreateTopicTest extends SpockTest {
    def setup() {
        createExternalCourseAndExecution()
    }

    def "create a topic"() {
        given:
        def topicDto = new TopicDto()
        topicDto.setName(TOPIC_1_NAME)

        when:
        topicService.createTopic(externalCourse.getId(), topicDto)

        then: "the topic is inside the repository"
        topicRepository.count() == 1L
        def result = topicRepository.findAll().get(0)
        result.getId() != null
        result.getName() == TOPIC_1_NAME
    }

    def "create a topic with the same name"() {
        given: "createQuestion a question"
        Topic topic = new Topic()
        topic.setName(TOPIC_1_NAME)
        topic.setCourse(externalCourse)
        externalCourse.addTopic(topic)
        topicRepository.save(topic)
        and: 'topic dto'
        def topicDto = new TopicDto()
        topicDto.setName(TOPIC_1_NAME)

        when: 'createQuestion another with the same name'
        topicService.createTopic(externalCourse.getId(), topicDto)

        then: "an error occurs"
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.DUPLICATE_TOPIC
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
