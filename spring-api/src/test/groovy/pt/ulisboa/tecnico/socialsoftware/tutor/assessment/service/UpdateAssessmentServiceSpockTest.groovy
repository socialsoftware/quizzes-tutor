package pt.ulisboa.tecnico.socialsoftware.tutor.assessment.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.AssessmentService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicConjunctionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.AssessmentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import spock.lang.Specification

@DataJpaTest
class UpdateAssessmentServiceSpockTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String ASSESSMENT_TITLE_1 = 'assessment title 1'
    public static final String ASSESSMENT_TITLE_2 = 'assessment title 2'
    public static final String TOPIC_NAME_1 = "topic name 1"
    public static final String TOPIC_NAME_2 = "topic name 2"

    @Autowired
    AssessmentService assessmentService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    AssessmentRepository assessmentRepository

    @Autowired
    TopicRepository topicRepository

    def assessmentDto
    def topicList

    def setup() {
        assessmentDto = new AssessmentDto()
        assessmentDto.setTitle(ASSESSMENT_TITLE_1)
        assessmentDto.setStatus(Assessment.Status.AVAILABLE.name())
        def topicConjunction = new TopicConjunctionDto()
        def topic = new Topic()
        topic.setName(TOPIC_NAME_1)
        topicRepository.save(topic)
        topicList = new ArrayList()
        topicList.add(new TopicDto(topic))
        topicConjunction.setTopics(topicList)
        def topicConjunctionList = new ArrayList()
        topicConjunctionList.add(topicConjunction)
        assessmentDto.setTopicConjunctions(topicConjunctionList)

        def course = new Course(COURSE_NAME)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM)
        courseExecutionRepository.save(courseExecution)

        assessmentDto = assessmentService.createAssessment(ACRONYM, ACADEMIC_TERM, assessmentDto)
    }

    def "update an assessment title and single topic conjuntion"() {
        given: "a created assessment"
        assessmentDto.setTitle(ASSESSMENT_TITLE_2)
        assessmentDto.setStatus(Assessment.Status.DISABLED.name())
        def topicConjunction = new TopicConjunctionDto()
        def topic = new Topic()
        topic.setName(TOPIC_NAME_2)
        topicRepository.save(topic)
        topicList = new ArrayList()
        topicList.add(new TopicDto(topic))
        topicConjunction.setTopics(topicList)
        def topicConjunctionList = new ArrayList()
        topicConjunctionList.add(topicConjunction)
        assessmentDto.setTopicConjunctions(topicConjunctionList)

        when:
        assessmentDto = assessmentService.updateAssessment(assessmentDto.getId(), assessmentDto)

        then: "the updated assessment is inside the repository"
        assessmentRepository.count() == 1L
        def result = assessmentRepository.findAll().get(0)
        result.getId() != null
        result.getStatus() == Assessment.Status.DISABLED
        result.getTitle() == ASSESSMENT_TITLE_2
        result.getTopicConjunctions().size() == 1
        def resTopicConjunction = result.getTopicConjunctions().first()
        resTopicConjunction.getId() != null
        resTopicConjunction.getTopics().size() == 1
        def resTopic = resTopicConjunction.getTopics().first()
        resTopic.getId() != null
        resTopic.getName() == TOPIC_NAME_2
    }

    def "update an assessment adding a topic, a conjuntion with topic and an empty conjunction"() {
        given: "a created assessment"
        def topic = new Topic()
        topic.setName(TOPIC_NAME_2)
        topicRepository.save(topic)

        topicList.add(new TopicDto(topic))
        assessmentDto.getTopicConjunctions()[0].setTopics(topicList)

        def newTopicConjunction = new TopicConjunctionDto()
        newTopicConjunction.topics.add(new TopicDto(topic))
        assessmentDto.addTopicConjunction(newTopicConjunction)

        assessmentDto.addTopicConjunction(new TopicConjunctionDto())

        when:
        assessmentDto = assessmentService.updateAssessment(assessmentDto.getId(), assessmentDto)

        then: "the updated assessment is inside the repository"
        assessmentRepository.count() == 1L
        def result = assessmentRepository.findAll().get(0)
        result.getId() != null
        result.getStatus() == Assessment.Status.AVAILABLE
        result.getTitle() == ASSESSMENT_TITLE_1
        result.getTopicConjunctions().size() == 3
        def resTopicConjunction1 = result.getTopicConjunctions().get(0)
        def resTopicConjunction2 = result.getTopicConjunctions().get(1)
        def resTopicConjunction3 = result.getTopicConjunctions().get(2)
        resTopicConjunction1.topics.size() == 2
        resTopicConjunction2.topics.size() == 1
        resTopicConjunction3.topics.size() == 0
    }


    @TestConfiguration
    static class AssessmentServiceImplTestContextConfiguration {

        @Bean
        AssessmentService assessmentService() {
            return new AssessmentService()
        }
    }

}
