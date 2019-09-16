package pt.ulisboa.tecnico.socialsoftware.tutor.statement.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
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
import java.util.stream.Collectors

@DataJpaTest
class GetAvailableQuizzesServiceSpockTest extends Specification {
    @Autowired
    QuizService quizService

    @Autowired
    AnswerService answerService

    @Autowired
    StatementService statementService

    @Autowired
    UserRepository userRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    def user
    def question
    def quiz
    def quizQuestion

    def setup() {
        user = new User('name', 'username', User.Role.STUDENT, 1, 2019)
        given: "create a question"
        question = new Question()
        question.setNumber(1)
        and: 'a teacher quiz and quiz question'
        quiz = new Quiz()
        quiz.setNumber(1)
        quiz.setType(Quiz.QuizType.TEACHER.name())
        quiz.setAvailableDate(LocalDateTime.now().minusDays(1))
        quiz.setYear(2019)
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
        def statmentQuizDtos = statementService.getAvailableQuizzes(user)

        then: 'the quiz answer is created'
        quizAnswerRepository.count() == 1L
        def result = quizAnswerRepository.findAll().get(0)
        result.getUser() == user
        user.getQuizAnswers().size() == 1
        result.getQuiz() == quiz
        quiz.getQuizAnswers().size() == 1
        and: 'the return statement contains one quiz'
        statmentQuizDtos.size() == 1
        def statementResult = statmentQuizDtos.get(0)
        statementResult.getTitle() == quiz.getTitle()
        statementResult.getAvailableDate() == quiz.getAvailableDate().toString()
        statementResult.getQuizAnswerId() == result.getId()
        statementResult.getQuestions().size() == 1
    }

    def 'the quiz is not available'() {
        given:
        quiz.setAvailableDate(LocalDateTime.now().plusDays(1))

        when:
        def statmentQuizDtos = statementService.getAvailableQuizzes(user)

        then: 'the quiz answer is not created'
        quizAnswerRepository.count() == 0L
        user.getQuizAnswers().size() == 0
        quiz.getQuizAnswers().size() == 0
        and: 'the return statement is empty'
        statmentQuizDtos.size() == 0
    }

    def 'the quiz is already created'() {
        given:
        def quizAnswer = new QuizAnswer(user, quiz)
        quizAnswerRepository.save(quizAnswer)

        when:
        def statmentQuizDtos = statementService.getAvailableQuizzes(user)

        then: 'the quiz answer exists'
        quizAnswerRepository.count() == 1L
        def result = quizAnswerRepository.findAll().get(0)
        result.getUser() == user
        user.getQuizAnswers().size() == 1
        result.getQuiz() == quiz
        quiz.getQuizAnswers().size() == 1
        and: 'the return statement contains one quiz'
        statmentQuizDtos.size() == 1
        def statementResult = statmentQuizDtos.get(0)
        statementResult.getTitle() == quiz.getTitle()
        statementResult.getAvailableDate() == quiz.getAvailableDate().toString()
        statementResult.getQuizAnswerId() == result.getId()
        statementResult.getQuestions().size() == 1
    }

    def 'the quiz is completed'() {
        given:
        def quizAnswer = new QuizAnswer(user, quiz)
        quizAnswer.setCompleted(true)
        quizAnswerRepository.save(quizAnswer)

        when:
        def statmentQuizDtos = statementService.getAvailableQuizzes(user)

        then: 'the quiz answer exists'
        quizAnswerRepository.count() == 1L
        def result = quizAnswerRepository.findAll().get(0)
        result.getUser() == user
        user.getQuizAnswers().size() == 1
        result.getQuiz() == quiz
        quiz.getQuizAnswers().size() == 1
        and: 'the return statement is empty'
        statmentQuizDtos.size() == 0
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
        QuizService quizService() {
            return new QuizService()
        }
    }

}
