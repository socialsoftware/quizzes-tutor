package pt.ulisboa.tecnico.socialsoftware.tutor.statement.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.AssessmentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementCreationDto
import spock.lang.Specification

@DataJpaTest
class FilterByAssessmentTest extends Specification {
    public static final String QUESTION_TITLE_1 = 'question title 1'
    public static final String QUESTION_TITLE_2 = 'question title 2'
    public static final String QUESTION_TITLE_3 = 'question title 3'
    public static final String QUESTION_TITLE_4 = 'question title 4'
    public static final String QUESTION_TITLE_5 = 'question title 5'
    public static final String QUESTION_CONTENT_1 = 'question content 1'
    public static final String QUESTION_CONTENT_2 = 'question content 2'
    public static final String QUESTION_CONTENT_3 = 'question content 3'
    public static final String QUESTION_CONTENT_4 = 'question content 4'
    public static final String QUESTION_CONTENT_5 = 'question content 5'
    public static final String TOPIC_NAME_1 = 'topic name 1'
    public static final String TOPIC_NAME_2 = 'topic name 2'
    public static final String TOPIC_NAME_3 = 'topic name 3'
    public static final String ASSESSMENT_TITLE_1 = 'assessment title 1'
    public static final String ASSESSMENT_TITLE_2 = 'assessment title 2'

    @Autowired
    StatementService statementService

    @Autowired
    TopicRepository topicRepository

    @Autowired
    AssessmentRepository assessmentRepository

    def questionTopic1
    def questionTopic2
    def questionTopic1_2
    def questionNoTopic
    def questionTopic1Again
    def topic1
    def topic2
    def topic3
    def questionList

    def setup() {
        topic1 = new Topic()
        topic1.setName(TOPIC_NAME_1)
        topic1 = topicRepository.save(topic1)

        topic2 = new Topic()
        topic2.setName(TOPIC_NAME_2)
        topic2 = topicRepository.save(topic2)

        topic3 = new Topic()
        topic3.setName(TOPIC_NAME_3)
        topic3 = topicRepository.save(topic3)

        questionList = new ArrayList<>()

        questionTopic1 = new Question()
        questionTopic1.setTitle(QUESTION_TITLE_1)
        questionTopic1.setContent(QUESTION_CONTENT_1)
        questionTopic1.setKey(1)
        questionTopic1.addTopic(topic1)
        questionList.add(questionTopic1)

        questionTopic2 = new Question()
        questionTopic2.setTitle(QUESTION_TITLE_2)
        questionTopic2.setContent(QUESTION_CONTENT_2)
        questionTopic2.setKey(2)
        questionTopic2.addTopic(topic2)
        questionList.add(questionTopic2)

        questionTopic1_2 = new Question()
        questionTopic1_2.setTitle(QUESTION_TITLE_3)
        questionTopic1_2.setContent(QUESTION_CONTENT_3)
        questionTopic1_2.setKey(3)
        questionTopic1_2.addTopic(topic1)
        questionTopic1_2.addTopic(topic2)
        questionList.add(questionTopic1_2)

        questionNoTopic = new Question()
        questionNoTopic.setTitle(QUESTION_TITLE_4)
        questionNoTopic.setContent(QUESTION_CONTENT_4)
        questionNoTopic.setKey(4)
        questionList.add(questionNoTopic)

        questionTopic1Again = new Question()
        questionTopic1Again.setTitle(QUESTION_TITLE_5)
        questionTopic1Again.setContent(QUESTION_CONTENT_5)
        questionTopic1Again.setKey(5)
        questionTopic1Again.addTopic(topic1)
        questionList.add(questionTopic1Again)
    }

    def "check assessment topic 1"() {
        given: 'assessment with topic 1'
        def assessmentTopic1 = new Assessment()
        assessmentTopic1.setTitle(ASSESSMENT_TITLE_1)
        def topicConjunction1 = new TopicConjunction()
        topicConjunction1.addTopic(topic1)
        assessmentTopic1.addTopicConjunction(topicConjunction1)
        assessmentRepository.save(assessmentTopic1)
        def statementCreationDto = new StatementCreationDto()
        statementCreationDto.assessment = assessmentTopic1.id

        when:
        def result = statementService.filterByAssessment(questionList, statementCreationDto)

        then: "the result is question 1 and 5"
        result.size() == 2
        result.contains(questionTopic1)
        result.contains(questionTopic1Again)
    }

    def "check assessment topic 1 and 2"() {
        given: 'assessment with topic 1 and 2'
        def assessmentTopic1_and_2 = new Assessment()
        assessmentTopic1_and_2.setTitle(ASSESSMENT_TITLE_2)
        def topicConjunction1_and_2 = new TopicConjunction()
        topicConjunction1_and_2.addTopic(topic1)
        topicConjunction1_and_2.addTopic(topic2)
        assessmentTopic1_and_2.addTopicConjunction(topicConjunction1_and_2)
        assessmentRepository.save(assessmentTopic1_and_2)
        def statementCreationDto = new StatementCreationDto()
        statementCreationDto.assessment = assessmentTopic1_and_2.id

        when:
        def result = statementService.filterByAssessment(questionList, statementCreationDto)

        then: "the result is question 3"
        result.size() == 1
        result.contains(questionTopic1_2)
    }

    def "check assessment topic 1 or 2"() {
        given: 'assessment with topic 1 or 2'
        def assessmentTopic1_or_2 = new Assessment()
        assessmentTopic1_or_2.setTitle(ASSESSMENT_TITLE_1)
        def topicConjunction1 = new TopicConjunction()
        topicConjunction1.addTopic(topic1)
        assessmentTopic1_or_2.addTopicConjunction(topicConjunction1)
        def topicConjunction2 = new TopicConjunction()
        topicConjunction2.addTopic(topic2)
        assessmentTopic1_or_2.addTopicConjunction(topicConjunction2)
        assessmentRepository.save(assessmentTopic1_or_2)
        def statementCreationDto = new StatementCreationDto()
        statementCreationDto.assessment = assessmentTopic1_or_2.id

        when:
        def result = statementService.filterByAssessment(questionList, statementCreationDto)

        then: "the result is question 1, 2 and 5"
        result.size() == 3
        result.contains(questionTopic1)
        result.contains(questionTopic2)
        result.contains(questionTopic1Again)
    }


    @TestConfiguration
    static class StatementServiceImplTestContextConfiguration {

        @Bean
        StatementService statementService() {
            return new StatementService()
        }

        @Bean
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }
        @Bean
        AnswersXmlImport answersXmlImport() {
            return new AnswersXmlImport()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }

}
