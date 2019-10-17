package pt.ulisboa.tecnico.socialsoftware.tutor.assessment.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.AssessmentService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicConjunctionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.AssessmentRepository
import spock.lang.Specification

@DataJpaTest
class UpdateAssessmentServiceSpockTest extends Specification {
    public static final String ASSESSMENT_TITLE_1 = 'assessment title 1'
    public static final String ASSESSMENT_TITLE_2 = 'assessment title 2'
    public static final String TOPIC_NAME_1 = "topic name 1"
    public static final String TOPIC_NAME_2 = "topic name 2"

    @Autowired
    AssessmentService assessmentService

    @Autowired
    AssessmentRepository assessmentRepository

    def assessmentDto
    def topicList

    def setup() {
        assessmentDto = new AssessmentDto()
        assessmentDto.setTitle(ASSESSMENT_TITLE_1)
        assessmentDto.setStatus(Assessment.Status.AVAILABLE.name())
        def topicConjunction = new TopicConjunctionDto()
        def topic = new TopicDto()
        topic.setName(TOPIC_NAME_1)
        topicList = new ArrayList()
        topicList.add(topic)
        topicConjunction.setTopics(topicList)
        def topicConjunctionList = new ArrayList()
        topicConjunctionList.add(topicConjunction)
        assessmentDto.setTopicConjunctions(topicConjunctionList)
        assessmentDto = assessmentService.createAssessment(assessmentDto)
    }

        def "update an assessment title and topic conjuntions"() {
            given: "a created assessment"
            assessmentDto.setTitle(ASSESSMENT_TITLE_2)
            def topicConjunctions = assessmentDto.getTopicConjunctions()
            def topic2 = new TopicDto()
            topic2.setName(TOPIC_NAME_2)
            topicList = new ArrayList()
            topicList.add(topic2)

            when:
            assessmentDto = assessmentService.updateAssessment(assessmentDto.getId(), assessmentDto)

            then: "the correct assessment is inside the repository"
            assessmentRepository.count() == 1L
            def result = assessmentRepository.findAll().get(0)
            result.getId() != null
            result.getStatus() == Assessment.Status.AVAILABLE
            result.getTitle() == ASSESSMENT_TITLE_2
            result.getTopicConjunctions().size() == 1
            def resTopicConjunction = result.getTopicConjunctions().first()
            resTopicConjunction.getId() != null
            resTopicConjunction.getTopics().size() == 1
            def resTopic = resTopicConjunction.getTopics().first()
            resTopic.getId() != null
            resTopic.getName() == TOPIC_NAME_2
        }


    @TestConfiguration
    static class AssessmentServiceImplTestContextConfiguration {

        @Bean
        AssessmentService assessmentService() {
            return new AssessmentService()
        }
    }

}
