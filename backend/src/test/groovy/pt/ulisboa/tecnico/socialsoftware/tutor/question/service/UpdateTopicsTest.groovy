package pt.ulisboa.tecnico.socialsoftware.tutor.question.service


import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto

@DataJpaTest
class UpdateTopicsTest extends SpockTest {
    public static final String COURSE_NAME = "Arquitetura de Software"
    public static final String TOPIC_ONE = 'nameOne'
    public static final String TOPIC_TWO = 'nameTwo'
    public static final String TOPIC_THREE = 'nameThree'

    def course
    def question
    def topicDtoOne
    def topicDtoTwo
    def topicDtoThree
    def topicOne
    def topicTwo
    def topicThree

    def setup() {
        course = new Course()
        course.setName(COURSE_NAME)
        courseRepository.save(course)

        question = new Question()
        question.setKey(1)
        question.setTitle("Question Title")
        question.setContent("Question Content")
        question.setCourse(course)

        topicDtoOne = new TopicDto()
        topicDtoOne.setName(TOPIC_ONE)
        topicDtoTwo = new TopicDto()
        topicDtoTwo.setName(TOPIC_TWO)
        topicDtoThree = new TopicDto()
        topicDtoThree.setName(TOPIC_THREE)

        topicOne = new Topic(course, topicDtoOne)
        topicTwo = new Topic(course, topicDtoTwo)
        question.getTopics().add(topicOne)
        topicOne.getQuestions().add(question)
        question.getTopics().add(topicTwo)
        topicTwo.getQuestions().add(question)
        questionRepository.save(question)
        topicRepository.save(topicOne)
        topicRepository.save(topicTwo)

        topicThree = new Topic(course, topicDtoThree)
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
