package pt.ulisboa.tecnico.socialsoftware.tutor.statement.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.*
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementCreationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.util.stream.Collectors

@DataJpaTest
class GenerateStudentQuizTest extends Specification {
    static final USERNAME = 'username'
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"

    @Autowired
    StatementService statementService

    @Autowired
    QuizRepository quizRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    AssessmentRepository assessmentRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    TopicConjunctionRepository topicConjunctionRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    def user
    def courseExecution
    def questionOne
    def questionTwo
    def assessment

    def setup() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        course.addCourseExecution(courseExecution)
        courseExecution.setCourse(course)

        courseExecutionRepository.save(courseExecution)
        courseRepository.save(course)

        user = new User('name', USERNAME, 1, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user)

        def topic = new Topic();
        topic.setName("TOPIC")
        topic.setCourse(course)
        topicRepository.save(topic)

        questionOne = new Question()
        questionOne.setKey(1)
        questionOne.setStatus(Question.Status.AVAILABLE)
        questionOne.setCourse(course)
        course.addQuestion(questionOne)
        questionOne.addTopic(topic)
        topic.addQuestion(questionOne)

        questionTwo = new Question()
        questionTwo.setKey(2)
        questionTwo.setStatus(Question.Status.AVAILABLE)
        questionTwo.setCourse(course)
        course.addQuestion(questionTwo)
        questionTwo.addTopic(topic)
        topic.addQuestion(questionTwo)

        userRepository.save(user)
        questionRepository.save(questionOne)
        questionRepository.save(questionTwo)

        def topicConjunction = new TopicConjunction()
        topicConjunction.addTopic(topic)
        topic.addTopicConjunction(topicConjunction)
        topicConjunctionRepository.save(topicConjunction)

        assessment = new Assessment()
        assessment.setStatus(Assessment.Status.AVAILABLE)
        assessment.setCourseExecution(courseExecution)
        assessment.addTopicConjunction(topicConjunction)
        topicConjunction.setAssessment(assessment)
        assessmentRepository.save(assessment)

    }

    def 'generate quiz for one question and there are two questions available'() {
        given:
        def quizForm = new StatementCreationDto()
        quizForm.setNumberOfQuestions(1)
        quizForm.setAssessment(assessment.getId().toString())

        when:
        statementService.generateStudentQuiz(USERNAME, courseExecution.getId(), quizForm)

        then:
        quizRepository.count() == 1L
        def result = quizRepository.findAll().get(0)
        result.getQuizAnswers().size() == 1
        def resQuizAnswer = result.getQuizAnswers().stream().collect(Collectors.toList()).get(0)
        resQuizAnswer.getQuiz() == result
        resQuizAnswer.getUser() == user
        resQuizAnswer.getQuestionAnswers().size() == 1
        result.getQuizQuestions().size() == 1
        def resQuizQuestion = result.getQuizQuestions().stream().collect(Collectors.toList()).get(0)
        resQuizQuestion.getQuiz() == result
        resQuizQuestion.getQuestion() == questionOne || resQuizQuestion.getQuestion() == questionTwo
        (questionOne.getQuizQuestions().size() == 1 &&  questionTwo.getQuizQuestions().size() == 0) ||  (questionOne.getQuizQuestions().size() == 0 &&  questionTwo.getQuizQuestions().size() == 1)
        questionOne.getQuizQuestions().contains(resQuizQuestion) || questionTwo.getQuizQuestions().contains(resQuizQuestion)
    }

    def 'generate quiz for two question and there are two questions available'() {
        given:
        def quizForm = new StatementCreationDto()
        quizForm.setNumberOfQuestions(2)
        quizForm.setAssessment(assessment.getId().toString())

        when:
        statementService.generateStudentQuiz(USERNAME, courseExecution.getId(), quizForm)

        then:
        quizRepository.count() == 1L
        def result = quizRepository.findAll().get(0)
        result.getQuizAnswers().size() == 1
        def resQuizAnswer = result.getQuizAnswers().stream().collect(Collectors.toList()).get(0)
        resQuizAnswer.getQuiz() == result
        resQuizAnswer.getUser() == user
        resQuizAnswer.getQuestionAnswers().size() == 2
        result.getQuizQuestions().size() == 2
        result.getQuizQuestions().stream().map{quizQuestion -> quizQuestion.getQuestion()}.allMatch{question -> question == questionOne || question == questionTwo}
        questionOne.getQuizQuestions().size() == 1
        questionTwo.getQuizQuestions().size() == 1
    }

    def 'generate quiz for three question and there are two questions available'() {
        given:
        def quizForm = new StatementCreationDto()
        quizForm.setNumberOfQuestions(3)
        quizForm.setAssessment(assessment.getId().toString())

        when:
        statementService.generateStudentQuiz(USERNAME, courseExecution.getId(), quizForm)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == ErrorMessage.NOT_ENOUGH_QUESTIONS
        quizRepository.count() == 0L
    }

    @TestConfiguration
    static class QuizServiceImplTestContextConfiguration {

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
    }

}
