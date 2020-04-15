package pt.ulisboa.tecnico.socialsoftware.tutor.assessment.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.AssessmentService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicConjunctionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.AssessmentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicConjunctionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import spock.lang.Specification

@DataJpaTest
class UpdateAssessmentTest extends Specification {
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

    @Autowired
    TopicConjunctionRepository topicConjunctionRepository

    def assessment
    def topicConjunction

    def setup() {
       def topic = new Topic()
        topic.setName(TOPIC_NAME_1)
        topicRepository.save(topic)

        assessment = new Assessment()
        assessment.setTitle(ASSESSMENT_TITLE_1)
        assessment.setStatus(Assessment.Status.AVAILABLE)
        assessment.setSequence(1)
        assessmentRepository.save(assessment)

        topicConjunction = new TopicConjunction()
        topicConjunction.setAssessment(assessment)
        topicConjunction.addTopic(topic)
        topicConjunctionRepository.save(topicConjunction)

        assessment.addTopicConjunction(topicConjunction)
    }

    def "update an assessment title, remove its topicConjunction and adding a new one"() {
        given: "an assessment dto"
        def assessmentDto = new AssessmentDto(assessment)
        assessmentDto.setTitle(ASSESSMENT_TITLE_2)
        assessmentDto.setStatus(Assessment.Status.DISABLED.name())
        assessmentDto.setSequence(assessment.getSequence())
        and: "a new topic"
        def topic = new Topic()
        topic.setName(TOPIC_NAME_2)
        topicRepository.save(topic)
        and: "a new TopicConjunction"
        def topicConjunctionDto = new TopicConjunctionDto()
        topicConjunctionDto.addTopic(new TopicDto(topic))
        and: "a topicConjunction list without the old topicConjunction"
        def topicConjunctionList = new ArrayList()
        topicConjunctionList.add(topicConjunctionDto)
        assessmentDto.setTopicConjunctions(topicConjunctionList)

        when:
        def result = assessmentService.updateAssessment(assessment.getId(), assessmentDto)

        then: "the updated assessment is inside the repository"
        assessmentRepository.count() == 1L
        and: "has the correct values"
        assessmentRepository.findAll().get(0) == assessment
        assessment.getId() != null
        assessment.getStatus() == Assessment.Status.DISABLED
        assessment.getTitle() == ASSESSMENT_TITLE_2
        assessment.getTopicConjunctions().size() == 1
        def topicConjunction = assessment.getTopicConjunctions().first()
        topicConjunction.getId() != null
        topicConjunction.getTopics().size() == 1
        topicConjunction.getTopics().first().getName() == TOPIC_NAME_2
        and: "the returned dto has the correct values"
        result.getId() != null
        result.getStatus() == Assessment.Status.DISABLED.name()
        result.getTitle() == ASSESSMENT_TITLE_2
        result.getTopicConjunctions().size() == 1
        def resTopicConjunction = result.getTopicConjunctions().first()
        resTopicConjunction.getId() != null
        resTopicConjunction.getTopics().size() == 1
        def resTopic = resTopicConjunction.getTopics().first()
        resTopic.getId() != null
        resTopic.getName() == TOPIC_NAME_2
    }

    def "update an assessment adding a topic, a conjunction with topic and an empty conjunction"() {
        given: "an assessment dto"
        def assessmentDto = new AssessmentDto(assessment)
        assessmentDto.setTitle(ASSESSMENT_TITLE_1)
        assessmentDto.setStatus(Assessment.Status.AVAILABLE.name())
        assessmentDto.setSequence(assessment.getSequence())
        assessmentDto.setTopicConjunctions(new ArrayList())
        and: "a topic"
        def topic = new Topic()
        topic.setName(TOPIC_NAME_2)
        topicRepository.save(topic)
        and: "a conjunction"
        def newTopicConjunction = new TopicConjunctionDto()
        newTopicConjunction.topics.add(new TopicDto(topic))
        assessmentDto.addTopicConjunction(newTopicConjunction)
        and: "a empty conjunction"
        assessmentDto.addTopicConjunction(new TopicConjunctionDto())
        and: "the previous topic conjunction"
        assessmentDto.addTopicConjunction(new TopicConjunctionDto(topicConjunction))

        when:
        assessmentService.updateAssessment(assessment.getId(), assessmentDto)

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
        resTopicConjunction1.topics.size() == 1
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
