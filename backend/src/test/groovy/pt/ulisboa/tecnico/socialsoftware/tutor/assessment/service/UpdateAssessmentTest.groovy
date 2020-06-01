package pt.ulisboa.tecnico.socialsoftware.tutor.assessment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicConjunctionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto

@DataJpaTest
class UpdateAssessmentTest extends SpockTest {
    Assessment assessment
    TopicConjunction topicConjunction

    def setup() {
        def topic = new Topic()
        topic.setName(TOPIC_1_NAME)
        topic.setCourse(course)
        topicRepository.save(topic)

        assessment = new Assessment()
        assessment.setTitle(ASSESSMENT_1_TITLE)
        assessment.setStatus(Assessment.Status.AVAILABLE)
        assessment.setSequence(1)
        assessment.setCourseExecution(courseExecution)
        assessmentRepository.save(assessment)

        topicConjunction = new TopicConjunction()
        topicConjunction.addTopic(topic)
        topicConjunction.setAssessment(assessment)
        topicConjunctionRepository.save(topicConjunction)
    }

    def "update an assessment title, remove its topicConjunction and adding a new one"() {
        def topic = new Topic()
        topic.setName(TOPIC_2_NAME)
        topic.setCourse(course)
        topicRepository.save(topic)

        def topicConjunctionDto = new TopicConjunctionDto()
        topicConjunctionDto.addTopic(new TopicDto(topic))

        def topicConjunctionList = new ArrayList()
        topicConjunctionList.add(topicConjunctionDto)

        def assessmentDto = new AssessmentDto(assessment)
        assessmentDto.setTitle(ASSESSMENT_2_TITLE)
        assessmentDto.setStatus(Assessment.Status.DISABLED.name())
        assessmentDto.setSequence(assessment.getSequence())
        assessmentDto.setTopicConjunctions(topicConjunctionList)

        when:
        def result = assessmentService.updateAssessment(assessment.getId(), assessmentDto)

        then: "the updated assessment is inside the repository"
        assessmentRepository.count() == 1L
        and: "has the correct values"
        assessmentRepository.findAll().get(0) == assessment
        assessment.getId() != null
        assessment.getStatus() == Assessment.Status.DISABLED
        assessment.getTitle() == ASSESSMENT_2_TITLE
        assessment.getTopicConjunctions().size() == 1
        def topicConjunction = assessment.getTopicConjunctions().first()
        topicConjunction.getId() != null
        topicConjunction.getTopics().size() == 1
        topicConjunction.getTopics().first().getName() == TOPIC_2_NAME
        and: "the returned dto has the correct values"
        result.getId() != null
        result.getStatus() == Assessment.Status.DISABLED.name()
        result.getTitle() == ASSESSMENT_2_TITLE
        result.getTopicConjunctions().size() == 1
        def resTopicConjunction = result.getTopicConjunctions().first()
        resTopicConjunction.getId() != null
        resTopicConjunction.getTopics().size() == 1
        def resTopic = resTopicConjunction.getTopics().first()
        resTopic.getId() != null
        resTopic.getName() == TOPIC_2_NAME
    }

    def "update an assessment adding a topic, a conjunction with topic and an empty conjunction"() {
        def assessmentDto = new AssessmentDto(assessment)
        assessmentDto.setTitle(ASSESSMENT_1_TITLE)
        assessmentDto.setStatus(Assessment.Status.AVAILABLE.name())
        assessmentDto.setSequence(assessment.getSequence())
        assessmentDto.setTopicConjunctions(new ArrayList())

        def topic = new Topic()
        topic.setName(TOPIC_2_NAME)
        topic.setCourse(course)
        topicRepository.save(topic)

        def topicConjunctionDto = new TopicConjunctionDto()
        topicConjunctionDto.topics.add(new TopicDto(topic))
        assessmentDto.addTopicConjunction(topicConjunctionDto)

        assessmentDto.addTopicConjunction(new TopicConjunctionDto())
        assessmentDto.addTopicConjunction(new TopicConjunctionDto(topicConjunction))

        when:
        assessmentService.updateAssessment(assessment.getId(), assessmentDto)

        then: "the updated assessment is inside the repository"
        assessmentRepository.count() == 1L
        def result = assessmentRepository.findAll().get(0)
        result.getId() != null
        result.getStatus() == Assessment.Status.AVAILABLE
        result.getTitle() == ASSESSMENT_1_TITLE
        result.getTopicConjunctions().size() == 3
        def resTopicConjunction1 = result.getTopicConjunctions().get(0)
        def resTopicConjunction2 = result.getTopicConjunctions().get(1)
        def resTopicConjunction3 = result.getTopicConjunctions().get(2)
        resTopicConjunction1.topics.size() == 1
        resTopicConjunction2.topics.size() == 1
        resTopicConjunction3.topics.size() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
