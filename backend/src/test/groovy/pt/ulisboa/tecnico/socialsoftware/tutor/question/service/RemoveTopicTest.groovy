package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import spock.lang.Specification

@DataJpaTest
class RemoveTopicTest extends Specification {
    public static final String COURSE_NAME = "Arquitetura de Software"
    private static final String TOPIC_ONE = 'nameOne'
    private static final String TOPIC_TWO = 'nameTwo'
    private static final String TOPIC_THREE = 'nameThree'
    private static final Integer KEY = 1

    @Autowired
    TopicService topicService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    TopicRepository topicRepository

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
        question.setTitle("Question Title")
        question.setContent("Question Content")
        question.setKey(KEY)

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
