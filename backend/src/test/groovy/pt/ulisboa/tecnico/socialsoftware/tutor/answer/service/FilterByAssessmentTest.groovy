package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.TopicConjunction
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementCreationDto

@DataJpaTest
class FilterByAssessmentTest extends SpockTest {
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
        createExternalCourseAndExecution()

        topic1 = new Topic()
        topic1.setName(TOPIC_1_NAME)
        topic1.setCourse(externalCourse)
        topic1 = topicRepository.save(topic1)

        topic2 = new Topic()
        topic2.setName(TOPIC_2_NAME)
        topic2.setCourse(externalCourse)
        topic2 = topicRepository.save(topic2)

        topic3 = new Topic()
        topic3.setName(TOPIC_3_NAME)
        topic3.setCourse(externalCourse)
        topic3 = topicRepository.save(topic3)

        questionList = new ArrayList<>()

        questionTopic1 = new Question()
        questionTopic1.setCourse(externalCourse)
        questionTopic1.setTitle(QUESTION_1_TITLE)
        questionTopic1.setContent(QUESTION_1_CONTENT)
        questionTopic1.setKey(1)
        questionTopic1.addTopic(topic1)
        questionList.add(questionTopic1)

        questionTopic2 = new Question()
        questionTopic2.setCourse(externalCourse)
        questionTopic2.setTitle(QUESTION_2_TITLE)
        questionTopic2.setContent(QUESTION_2_CONTENT)
        questionTopic2.setKey(2)
        questionTopic2.addTopic(topic2)
        questionList.add(questionTopic2)

        questionTopic1_2 = new Question()
        questionTopic1_2.setCourse(externalCourse)
        questionTopic1_2.setTitle(QUESTION_3_TITLE)
        questionTopic1_2.setContent(QUESTION_3_CONTENT)
        questionTopic1_2.setKey(3)
        questionTopic1_2.addTopic(topic1)
        questionTopic1_2.addTopic(topic2)
        questionList.add(questionTopic1_2)

        questionNoTopic = new Question()
        questionNoTopic.setCourse(externalCourse)
        questionNoTopic.setTitle(QUESTION_4_TITLE)
        questionNoTopic.setContent(QUESTION_4_CONTENT)
        questionNoTopic.setKey(4)
        questionList.add(questionNoTopic)

        questionTopic1Again = new Question()
        questionTopic1Again.setCourse(externalCourse)
        questionTopic1Again.setTitle(QUESTION_5_TITLE)
        questionTopic1Again.setContent(QUESTION_5_CONTENT)
        questionTopic1Again.setKey(5)
        questionTopic1Again.addTopic(topic1)
        questionList.add(questionTopic1Again)
    }

    def "check assessment topic 1"() {
        given: 'assessment with topic 1'
        def assessmentTopic1 = new Assessment()
        assessmentTopic1.setTitle(ASSESSMENT_1_TITLE)
        assessmentTopic1.setCourseExecution(externalCourseExecution)
        assessmentRepository.save(assessmentTopic1)

        def topicConjunction1 = new TopicConjunction()
        topicConjunction1.addTopic(topic1)
        topicConjunction1.setAssessment(assessmentTopic1)

        def statementCreationDto = new StatementCreationDto()
        statementCreationDto.assessment = assessmentTopic1.id

        when:
        def result = answerService.filterByAssessment(questionList, statementCreationDto)

        then: "the result is question 1 and 5"
        result.size() == 2
        result.contains(questionTopic1)
        result.contains(questionTopic1Again)
    }

    def "check assessment topic 1 and 2"() {
        given: 'assessment with topic 1 and 2'
        def assessmentTopic1_and_2 = new Assessment()
        assessmentTopic1_and_2.setCourseExecution(externalCourseExecution)
        assessmentTopic1_and_2.setTitle(ASSESSMENT_2_TITLE)
        assessmentRepository.save(assessmentTopic1_and_2)

        def topicConjunction1_and_2 = new TopicConjunction()
        topicConjunction1_and_2.setAssessment(assessmentTopic1_and_2)
        topicConjunction1_and_2.addTopic(topic1)
        topicConjunction1_and_2.addTopic(topic2)

        def statementCreationDto = new StatementCreationDto()
        statementCreationDto.assessment = assessmentTopic1_and_2.id

        when:
        def result = answerService.filterByAssessment(questionList, statementCreationDto)

        then: "the result is question 3"
        result.size() == 1
        result.contains(questionTopic1_2)
    }

    def "check assessment topic 1 or 2"() {
        given: 'assessment with topic 1 or 2'
        def assessmentTopic1_or_2 = new Assessment()
        assessmentTopic1_or_2.setTitle(ASSESSMENT_1_TITLE)
        assessmentTopic1_or_2.setCourseExecution(externalCourseExecution)
        assessmentRepository.save(assessmentTopic1_or_2)

        def topicConjunction1 = new TopicConjunction()
        topicConjunction1.addTopic(topic1)
        topicConjunction1.setAssessment(assessmentTopic1_or_2)

        def topicConjunction2 = new TopicConjunction()
        topicConjunction2.addTopic(topic2)
        topicConjunction2.setAssessment(assessmentTopic1_or_2)

        def statementCreationDto = new StatementCreationDto()
        statementCreationDto.assessment = assessmentTopic1_or_2.id

        when:
        def result = answerService.filterByAssessment(questionList, statementCreationDto)

        then: "the result is question 1, 2 and 5"
        result.size() == 3
        result.contains(questionTopic1)
        result.contains(questionTopic2)
        result.contains(questionTopic1Again)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
