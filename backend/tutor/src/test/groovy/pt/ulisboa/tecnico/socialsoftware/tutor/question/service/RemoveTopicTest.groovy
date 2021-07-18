package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic

@DataJpaTest
class RemoveTopicTest extends SpockTest {
    def question
    def topicOne
    def topicTwo
    def topicThree

    def setup() {
        createExternalCourseAndExecution()

        topicOne = new Topic()
        topicOne.setName(TOPIC_1_NAME)
        topicOne.setCourse(externalCourse)
        topicRepository.save(topicOne)

        topicTwo = new Topic()
        topicTwo.setName(TOPIC_2_NAME)
        topicTwo.setCourse(externalCourse)
        topicRepository.save(topicTwo)

        topicThree = new Topic()
        topicThree.setName(TOPIC_3_NAME)
        topicThree.setCourse(externalCourse)
        topicRepository.save(topicThree)

        question = new Question()
        question.setCourse(externalCourse)
        question.setTitle("Question Title")
        question.setContent("Question Content")
        question.addTopic(topicOne)
        question.addTopic(topicTwo)
        questionRepository.save(question)
    }

    def "remove topic"() {
        when:
        topicService.removeTopic(topicOne.getId())

        then:
        topicOne.getQuestions().size() == 0
        topicTwo.getQuestions().size() == 1
        question.getTopics().size() == 1
        question.getTopics().contains(topicTwo)
    }

    def "remove topic has not question"() {
        when:
        topicService.removeTopic(topicThree.getId())

        then:
        topicRepository.findAll().size() == 2
        topicOne.getQuestions().size() == 1
        topicTwo.getQuestions().size() == 1
        question.getTopics().size() == 2
        question.getTopics().contains(topicOne)
        question.getTopics().contains(topicTwo)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
