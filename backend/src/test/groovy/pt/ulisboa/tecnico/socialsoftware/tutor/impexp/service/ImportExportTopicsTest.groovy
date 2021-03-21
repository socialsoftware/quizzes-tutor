package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto

@DataJpaTest
class ImportExportTopicsTest extends SpockTest {

    def topicDtoOne
    def topicDtoTwo

    def setup() {
        createExternalCourseAndExecution()

        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        questionDto = questionService.createQuestion(externalCourse.id, questionDto)

        topicDtoOne = new TopicDto()
        topicDtoOne.setName(TOPIC_1_NAME)
        topicDtoTwo = new TopicDto()
        topicDtoTwo.setName(TOPIC_2_NAME)

        topicDtoOne = topicService.createTopic(externalCourse.id, topicDtoOne)
        topicDtoTwo = topicService.createTopic(externalCourse.id, topicDtoTwo)

        TopicDto[] topics = [topicDtoOne, topicDtoTwo]
        questionService.updateQuestionTopics(questionDto.getId(), topics)
    }

    def 'export and import topics'() {
        given: 'a xml with questions'
        def topicsXml = topicService.exportTopics()
        and: 'delete topics'
        topicService.removeTopic(topicDtoOne.getId())
        topicService.removeTopic(topicDtoTwo.getId())

        when:
        topicService.importTopics(topicsXml)

        then:
        topicRepository.findAll().size() == 2
        def topicOne = topicRepository.findAll().get(0)
        def topicTwo = topicRepository.findAll().get(1)
        topicOne.getName() == TOPIC_1_NAME && topicTwo.getName() == TOPIC_2_NAME ||
                topicOne.getName() == TOPIC_2_NAME && topicTwo.getName() == TOPIC_1_NAME

        topicOne.getQuestions().size() == 1
        topicTwo.getQuestions().size() == 1
        topicOne.getQuestions() == topicTwo.getQuestions()

        Question question = topicOne.getQuestions().stream().findAny().orElse(null)
        question.getTopics().size() == 2
        question.getTopics().stream().map{topic -> topic.getName()}
                .allMatch{topic -> topic == TOPIC_1_NAME || topic == TOPIC_2_NAME }
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
