package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import spock.lang.Specification

@DataJpaTest
class ImportExportTopicsTest extends Specification {
    public static final String COURSE_NAME = "Arquitetura de Software"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String TOPIC_ONE = "topicOne"
    public static final String TOPIC_TWO = "topicTwo"

    @Autowired
    QuestionService questionService

    @Autowired
    TopicService topicService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TopicRepository topicRepository

    def topicDtoOne
    def topicDtoTwo

    def setup() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)

        questionDto = questionService.createQuestion(course.id, questionDto)

        topicDtoOne = new TopicDto()
        topicDtoOne.setName(TOPIC_ONE)
        topicDtoTwo = new TopicDto()
        topicDtoTwo.setName(TOPIC_TWO)

        topicDtoOne = topicService.createTopic(course.id, topicDtoOne)
        topicDtoTwo = topicService.createTopic(course.id, topicDtoTwo)

        TopicDto[] topics = [topicDtoOne, topicDtoTwo]
        questionService.updateQuestionTopics(questionDto.getId(), topics)
    }

    def 'export and import topics'() {
        given: 'a xml with questions'
        def topicsXml = topicService.exportTopics()
        System.out.println(topicsXml)
        and: 'delete topics'
        topicService.removeTopic(topicDtoOne.getId())
        topicService.removeTopic(topicDtoTwo.getId())

        when:
        topicService.importTopics(topicsXml)

        then:
        topicRepository.findAll().size() == 2
        def topicOne = topicRepository.findAll().get(0)
        def topicTwo = topicRepository.findAll().get(1)
        topicOne.getName() == TOPIC_ONE && topicTwo.getName() == TOPIC_TWO ||
                topicOne.getName() == TOPIC_TWO && topicTwo.getName() == TOPIC_ONE

        topicOne.getQuestions().size() == 1
        topicTwo.getQuestions().size() == 1
        topicOne.getQuestions() == topicTwo.getQuestions()

        def question = topicOne.getQuestions().stream().findAny().orElse(null)
        question.getTopics().size() == 2
        question.getTopics().stream().map{topic -> topic.getName()}
                .allMatch{topic -> topic == TOPIC_ONE || topic == TOPIC_TWO}
    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {
        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        TopicService topicService() {
            return new TopicService()
        }
    }
}
