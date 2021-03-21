package pt.ulisboa.tecnico.socialsoftware.tutor.execution.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.TopicConjunctionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto

@DataJpaTest
class CreateAssessmentTest extends SpockTest {
    def setup() {
        createExternalCourseAndExecution()
    }

    def "create a assessment with one topicConjunction with one topic"() {
        given: "a assessmentDto"
        def topic = new Topic()
        topic.setCourse(externalCourse)
        topic.setName(TOPIC_1_NAME)
        topic = topicRepository.save(topic)

        def topicList = new ArrayList()
        topicList.add(new TopicDto(topic))

        def topicConjunction = new TopicConjunctionDto()
        topicConjunction.setTopics(topicList)

        def topicConjunctionList = new ArrayList()
        topicConjunctionList.add(topicConjunction)

        def assessmentDto = new AssessmentDto()
        assessmentDto.setTitle(ASSESSMENT_1_TITLE)
        assessmentDto.setStatus(Assessment.Status.AVAILABLE.name())
        assessmentDto.setTopicConjunctions(topicConjunctionList)

        when:
        assessmentService.createAssessment(externalCourseExecution.getId(), assessmentDto)

        then: "the correct assessment is inside the repository"
        assessmentRepository.count() == 1L
        topicRepository.count() == 1L
        def result = courseExecutionRepository.findByFields(COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO.name())
                .get().getAssessments().stream().findAny().get()
        result.getId() != null
        result.getStatus() == Assessment.Status.AVAILABLE
        result.getTitle() == ASSESSMENT_1_TITLE
        result.getCourseExecution() == externalCourseExecution
        result.getTopicConjunctions().size() == 1
        def resTopicConjunction = result.getTopicConjunctions().first()
        resTopicConjunction.getId() != null
        resTopicConjunction.getTopics().size() == 1
        def resTopic = resTopicConjunction.getTopics().first()
        resTopic.getId() != null
        resTopic.getName() == TOPIC_1_NAME
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
