package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto

@DataJpaTest
class UpdateTopicsTest extends SpockTest {
    def question
    def topicDtoOne
    def topicDtoTwo
    def topicDtoThree
    def topicOne
    def topicTwo
    def topicThree

    def setup() {
        createExternalCourseAndExecution()

        question = new Question()
        question.setKey(1)
        question.setTitle("Question Title")
        question.setContent("Question Content")
        question.setCourse(externalCourse)

        topicDtoOne = new TopicDto()
        topicDtoOne.setName(TOPIC_1_NAME)
        topicDtoTwo = new TopicDto()
        topicDtoTwo.setName(TOPIC_2_NAME)
        topicDtoThree = new TopicDto()
        topicDtoThree.setName(TOPIC_3_NAME)

        topicOne = new Topic(externalCourse, topicDtoOne)
        topicTwo = new Topic(externalCourse, topicDtoTwo)
        question.getTopics().add(topicOne)
        topicOne.getQuestions().add(question)
        question.getTopics().add(topicTwo)
        topicTwo.getQuestions().add(question)
        questionRepository.save(question)
        topicRepository.save(topicOne)
        topicRepository.save(topicTwo)

        topicThree = new Topic(externalCourse, topicDtoThree)
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
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
