package pt.ulisboa.tecnico.socialsoftware.tutor.statement.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class GetAvailableQuizzesTest extends Specification {
    static final USERNAME = 'username'
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"

    @Autowired
    QuizService quizService

    @Autowired
    AnswerService answerService

    @Autowired
    StatementService statementService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    def user
    def courseExecution
    def question
    def quiz
    def quizQuestion

    def setup() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User('name', USERNAME, 1, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user)

        question = new Question()
        question.setKey(1)
        question.setCourse(course)
        course.addQuestion(question)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.PROPOSED)
        quiz.setAvailableDate(LocalDateTime.now().minusDays(1))
        quiz.setCourseExecution(courseExecution)
        courseExecution.addQuiz(quiz)

        quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(1)

        quiz.addQuizQuestion(quizQuestion)
        quizQuestion.setQuiz(quiz)
        question.addQuizQuestion(quizQuestion)
        quizQuestion.setQuestion(question)

        userRepository.save(user)
        quizRepository.save(quiz)
        questionRepository.save(question)
    }

    def 'get the quizzes for the student'() {
        when:
        def statementQuizDtos = statementService.getAvailableQuizzes(USERNAME, courseExecution.getId())

        then: 'the quiz answer is created'
        quizAnswerRepository.count() == 1L
        def result = quizAnswerRepository.findAll().get(0)
        result.getUser() == user
        user.getQuizAnswers().size() == 1
        result.getQuiz() == quiz
        quiz.getQuizAnswers().size() == 1
        and: 'the return statement contains one quiz'
        statementQuizDtos.size() == 1
        def statementResult = statementQuizDtos.get(0)
        statementResult.getTitle() == quiz.getTitle()
        statementResult.getAvailableDate() == quiz.getAvailableDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        statementResult.getQuizAnswerId() == result.getId()
        statementResult.getQuestions().size() == 1
    }

    def 'the quiz is not available'() {
        given:
        quiz.setAvailableDate(LocalDateTime.now().plusDays(1))

        when:
        def statementQuizDtos = statementService.getAvailableQuizzes(USERNAME, courseExecution.getId())

        then: 'the quiz answer is not created'
        quizAnswerRepository.count() == 0
        user.getQuizAnswers().size() == 0
        quiz.getQuizAnswers().size() == 0
        and: 'the return statement is empty'
        statementQuizDtos.size() == 0
    }

    def 'the quiz is already created'() {
        given:
        def quizAnswer = new QuizAnswer(user, quiz)
        quizAnswerRepository.save(quizAnswer)

        when:
        def statementQuizDtos = statementService.getAvailableQuizzes(USERNAME, courseExecution.getId())

        then: 'the quiz answer exists'
        quizAnswerRepository.count() == 1L
        def result = quizAnswerRepository.findAll().get(0)
        result.getUser() == user
        user.getQuizAnswers().size() == 1
        result.getQuiz() == quiz
        quiz.getQuizAnswers().size() == 1
        and: 'the return statement contains one quiz'
        statementQuizDtos.size() == 1
        def statementResult = statementQuizDtos.get(0)
        statementResult.getTitle() == quiz.getTitle()
        statementResult.getAvailableDate() == quiz.getAvailableDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        statementResult.getQuizAnswerId() == result.getId()
        statementResult.getQuestions().size() == 1
    }

    def 'the quiz is completed'() {
        given:
        def quizAnswer = new QuizAnswer(user, quiz)
        quizAnswer.setCompleted(true)
        quizAnswerRepository.save(quizAnswer)

        when:
        def statementQuizDtos = statementService.getAvailableQuizzes(USERNAME, courseExecution.getId())

        then: 'the quiz answer exists'
        quizAnswerRepository.count() == 1L
        def result = quizAnswerRepository.findAll().get(0)
        result.getUser() == user
        user.getQuizAnswers().size() == 1
        result.getQuiz() == quiz
        quiz.getQuizAnswers().size() == 1
        and: 'the return statement is empty'
        statementQuizDtos.size() == 0
     }

    @TestConfiguration
    static class QuizServiceImplTestContextConfiguration {
        @Bean
        StatementService statementService() {
            return new StatementService()
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
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}
