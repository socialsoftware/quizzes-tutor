package pt.ulisboa.tecnico.socialsoftware.tutor.execution.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.TopicConjunction
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.TopicConjunctionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto

@DataJpaTest
class UpdateAssessmentTest extends SpockTest {
    def assessmentId
    def topicConjunction

    def setup() {
        createExternalCourseAndExecution()

        def topic = new Topic()
        topic.setName(TOPIC_1_NAME)
        topic.setCourse(externalCourse)
        topicRepository.save(topic)

        def assessment = new Assessment()
        assessment.setTitle(ASSESSMENT_1_TITLE)
        assessment.setStatus(Assessment.Status.AVAILABLE)
        assessment.setSequence(1)
        assessment.setCourseExecution(externalCourseExecution)
        assessmentRepository.save(assessment)
        assessmentId = assessment.id

        topicConjunction = new TopicConjunction()
        topicConjunction.addTopic(topic)
        topicConjunction.setAssessment(assessment)
        topicConjunctionRepository.save(topicConjunction)
    }

    def "update an assessment title, remove its topicConjunction and adding a new one"() {
        def topic = new Topic()
        topic.setName(TOPIC_2_NAME)
        topic.setCourse(externalCourse)
        topicRepository.save(topic)

        def topicConjunctionDto = new TopicConjunctionDto()
        topicConjunctionDto.addTopic(new TopicDto(topic))

        def topicConjunctionList = new ArrayList()
        topicConjunctionList.add(topicConjunctionDto)

        def assessmentDto = new AssessmentDto()
        assessmentDto.id = assessmentId
        assessmentDto.setTitle(ASSESSMENT_2_TITLE)
        assessmentDto.setStatus(Assessment.Status.DISABLED.name())
        assessmentDto.setSequence(2)
        assessmentDto.setTopicConjunctions(topicConjunctionList)

        when:
        def resultDto = assessmentService.updateAssessment(assessmentId, assessmentDto)

        then: "the updated assessment is inside the repository"
        assessmentRepository.count() == 1L
        and: "has the correct values"
        def assessmentResult = assessmentRepository.findAll().get(0)
        assessmentResult.getId() != null
        assessmentResult.getStatus() == Assessment.Status.DISABLED
        assessmentResult.getTitle() == ASSESSMENT_2_TITLE
        assessmentResult.getTopicConjunctions().size() == 1
        def topicConjunctionResult = assessmentResult.getTopicConjunctions().first()
        topicConjunctionResult.getId() != null
        topicConjunctionResult.getTopics().size() == 1
        topicConjunctionResult.getTopics().first().getName() == TOPIC_2_NAME
        and: "the returned dto has the correct values"
        resultDto.getId() != null
        resultDto.getStatus() == Assessment.Status.DISABLED.name()
        resultDto.getTitle() == ASSESSMENT_2_TITLE
        resultDto.getTopicConjunctions().size() == 1
        def resTopicConjunctionDto = resultDto.getTopicConjunctions().first()
        resTopicConjunctionDto.getId() != null
        resTopicConjunctionDto.getTopics().size() == 1
        def resTopicDto = resTopicConjunctionDto.getTopics().first()
        resTopicDto.getId() != null
        resTopicDto.getName() == TOPIC_2_NAME
    }

    def "update an assessment adding a topic, a conjunction with topic and an empty conjunction"() {
        def topic = new Topic()
        topic.setName(TOPIC_2_NAME)
        topic.setCourse(externalCourse)
        topicRepository.save(topic)

        def topicConjunctionDto = new TopicConjunctionDto()
        topicConjunctionDto.addTopic(new TopicDto(topic))

        def topicConjunctionList = new ArrayList()
        topicConjunctionList.add(new TopicConjunctionDto())
        topicConjunctionList.add(topicConjunctionDto)
        topicConjunctionList.add(new TopicConjunctionDto(topicConjunction))

        def assessmentDto = new AssessmentDto()
        assessmentDto.id = assessmentId
        assessmentDto.setTitle(ASSESSMENT_1_TITLE)
        assessmentDto.setStatus(Assessment.Status.AVAILABLE.name())
        assessmentDto.setSequence(3)
        assessmentDto.setTopicConjunctions(topicConjunctionList)

        when:
        assessmentService.updateAssessment(assessmentId, assessmentDto)

        then: "the updated assessment is inside the repository"
        assessmentRepository.count() == 1L
        def result = assessmentRepository.findAll().get(0)
        result.getId() != null
        result.getStatus() == Assessment.Status.AVAILABLE
        result.getTitle() == ASSESSMENT_1_TITLE
        result.getSequence() == 3
        result.getTopicConjunctions().size() == 3
        def resTopicConjunction1 = result.getTopicConjunctions().get(0)
        def resTopicConjunction2 = result.getTopicConjunctions().get(1)
        def resTopicConjunction3 = result.getTopicConjunctions().get(2)
        resTopicConjunction1.topics.size() == 1
        resTopicConjunction2.topics.size() == 0
        resTopicConjunction3.topics.size() == 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
