import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic

@DataJpaTest
class RemoveTopicTest extends SpockTest {
    public static final String COURSE_NAME = "Arquitetura de Software"
    private static final String TOPIC_ONE = 'nameOne'
    private static final String TOPIC_TWO = 'nameTwo'
    private static final String TOPIC_THREE = 'nameThree'
    private static final Integer KEY = 1


    def question
    def topicOne
    def topicTwo
    def topicThree

    def setup() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, "ACRONYM", "ACADEMIC_TERM", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        topicOne = new Topic()
        topicOne.setName(TOPIC_ONE)
        topicOne.setCourse(course)
        topicRepository.save(topicOne)

        topicTwo = new Topic()
        topicTwo.setName(TOPIC_TWO)
        topicTwo.setCourse(course)
        topicRepository.save(topicTwo)

        topicThree = new Topic()
        topicThree.setName(TOPIC_THREE)
        topicThree.setCourse(course)
        topicRepository.save(topicThree)

        question = new Question()
        question.setCourse(course)
        question.setTitle("Question Title")
        question.setContent("Question Content")
        question.setKey(KEY)
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
