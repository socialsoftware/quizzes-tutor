package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementCreationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.TopicConjunction
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser

import java.util.stream.Collectors

@DataJpaTest
class GenerateStudentQuizTest extends SpockTest {
    def user
    def questionOne
    def questionTwo
    def assessment
    def questionDetails

    def setup() {
        createExternalCourseAndExecution()

        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)

        def topic = new Topic()
        topic.setName("TOPIC")
        topic.setCourse(externalCourse)
        topicRepository.save(topic)

        questionOne = new Question()
        questionOne.setKey(1)
        questionOne.setContent("Question Content")
        questionOne.setTitle("Question Title")
        questionOne.setStatus(Question.Status.AVAILABLE)
        questionOne.setCourse(externalCourse)
        questionOne.addTopic(topic)
        questionDetails = new MultipleChoiceQuestion()
        questionOne.setQuestionDetails(questionDetails)
        questionRepository.save(questionOne)

        questionTwo = new Question()
        questionTwo.setKey(2)
        questionTwo.setContent("Question Content")
        questionTwo.setTitle("Question Title")
        questionTwo.setStatus(Question.Status.AVAILABLE)
        questionTwo.setCourse(externalCourse)
        questionTwo.addTopic(topic)
        questionDetails = new MultipleChoiceQuestion()
        questionTwo.setQuestionDetails(questionDetails)
        questionRepository.save(questionTwo)

        assessment = new Assessment()
        assessment.setTitle("Assessment title")
        assessment.setStatus(Assessment.Status.AVAILABLE)
        assessment.setCourseExecution(externalCourseExecution)
        def topicConjunction = new TopicConjunction()
        topicConjunction.addTopic(topic)
        topicConjunction.setAssessment(assessment)
        assessmentRepository.save(assessment)

    }

    def 'generate quiz for one question and there are two questions available'() {
        given:
        def quizForm = new StatementCreationDto()
        quizForm.setNumberOfQuestions(1)
        quizForm.setAssessment(assessment.getId())

        when:
        answerService.generateStudentQuiz(user.getId(), externalCourseExecution.getId(), quizForm)

        then:
        quizRepository.count() == 1L
        def result = quizRepository.findAll().get(0)
        result.getQuizAnswers().size() == 1
        def resQuizAnswer = result.getQuizAnswers().stream().collect(Collectors.toList()).get(0)
        resQuizAnswer.getQuiz() == result
        resQuizAnswer.getUser() == user
        resQuizAnswer.getQuestionAnswers().size() == 1
        result.getQuizQuestionsNumber() == 1
        def resQuizQuestion = result.getQuizQuestions().get(0)
        resQuizQuestion.getQuiz() == result
        resQuizQuestion.getQuestion() == questionOne || resQuizQuestion.getQuestion() == questionTwo
        (questionOne.getQuizQuestions().size() == 1 &&  questionTwo.getQuizQuestions().size() == 0) ||  (questionOne.getQuizQuestions().size() == 0 &&  questionTwo.getQuizQuestions().size() == 1)
        questionOne.getQuizQuestions().contains(resQuizQuestion) || questionTwo.getQuizQuestions().contains(resQuizQuestion)
    }

    def 'generate quiz for two question and there are two questions available'() {
        given:
        def quizForm = new StatementCreationDto()
        quizForm.setNumberOfQuestions(2)
        quizForm.setAssessment(assessment.getId())

        when:
        answerService.generateStudentQuiz(user.getId(), externalCourseExecution.getId(), quizForm)

        then:
        quizRepository.count() == 1L
        def result = quizRepository.findAll().get(0)
        result.getQuizAnswers().size() == 1
        def resQuizAnswer = result.getQuizAnswers().stream().collect(Collectors.toList()).get(0)
        resQuizAnswer.getQuiz() == result
        resQuizAnswer.getUser() == user
        resQuizAnswer.getQuestionAnswers().size() == 2
        result.getQuizQuestionsNumber() == 2
        result.getQuizQuestions().stream().map{quizQuestion -> quizQuestion.getQuestion()}.allMatch{question -> question == questionOne || question == questionTwo}
        questionOne.getQuizQuestions().size() == 1
        questionTwo.getQuizQuestions().size() == 1
    }

    def 'generate quiz for three question and there are two questions available'() {
        given:
        def quizForm = new StatementCreationDto()
        quizForm.setNumberOfQuestions(3)
        quizForm.setAssessment(assessment.getId())

        when:
        answerService.generateStudentQuiz(user.getId(), externalCourseExecution.getId(), quizForm)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == ErrorMessage.NOT_ENOUGH_QUESTIONS
        quizRepository.count() == 0L
    }

    def 'cannot generate quiz because there is no assessment'() {
        given:
        def quizForm = new StatementCreationDto()
        quizForm.setNumberOfQuestions(1)

        when:
        answerService.generateStudentQuiz(user.getId(), externalCourseExecution.getId(), quizForm)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == ErrorMessage.NOT_ENOUGH_QUESTIONS
        quizRepository.count() == 0L
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
