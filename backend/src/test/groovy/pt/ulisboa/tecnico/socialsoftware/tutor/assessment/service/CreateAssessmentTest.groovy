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
class CreateAssessmentTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String ASSESSMENT_TITLE = 'assessment title'
    public static final String TOPIC_NAME = "topic name"

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

    def course
    def courseExecution

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
    }

    def "create a assessment with one topicConjunction with one topic"() {
        given: "a assessmentDto"
        def assessmentDto = new AssessmentDto()
        assessmentDto.setTitle(ASSESSMENT_TITLE)
        assessmentDto.setStatus(Assessment.Status.AVAILABLE.name())
        def topicConjunction = new TopicConjunctionDto()
        def topic = new Topic()
        topic.setName(TOPIC_NAME)
        topic = topicRepository.save(topic)
        def topicList = new ArrayList()
        topicList.add(new TopicDto(topic))
        topicConjunction.setTopics(topicList)
        def topicConjunctionList = new ArrayList()
        topicConjunctionList.add(topicConjunction)
        assessmentDto.setTopicConjunctions(topicConjunctionList)

        when:
        assessmentService.createAssessment(courseExecution.getId(), assessmentDto)

        then: "the correct assessment is inside the repository"
        courseExecutionRepository.count() == 1L
        assessmentRepository.count() == 1L
        topicRepository.count() == 1L
        def result = courseExecutionRepository.findAll().get(0).getAssessments().stream().findAny().get()
        result.getId() != null
        result.getStatus() == Assessment.Status.AVAILABLE
        result.getTitle() == ASSESSMENT_TITLE
        result.getCourseExecution() == courseExecution
        result.getTopicConjunctions().size() == 1
        def resTopicConjunction = result.getTopicConjunctions().first()
        resTopicConjunction.getId() != null
        resTopicConjunction.getTopics().size() == 1
        def resTopic = resTopicConjunction.getTopics().first()
        resTopic.getId() != null
        resTopic.getName() == TOPIC_NAME
    }

    @TestConfiguration
    static class AssessmentServiceImplTestContextConfiguration {

        @Bean
        AssessmentService assessmentService() {
            return new AssessmentService()
        }
    }
}
