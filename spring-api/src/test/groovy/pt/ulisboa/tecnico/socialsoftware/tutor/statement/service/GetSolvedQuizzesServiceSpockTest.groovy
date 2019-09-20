package pt.ulisboa.tecnico.socialsoftware.tutor.statement.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
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

@DataJpaTest
class GetSolvedQuizzesServiceSpockTest extends Specification {
    static final USERNAME = 'username'

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

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    def user
    def question
    def option
    def quiz
    def quizQuestion

    def setup() {
        user = new User('name', USERNAME, User.Role.STUDENT, 1, 2019)
        given: "create a question"
        question = new Question()
        question.setNumber(1)
        option = new Option()
        option.setCorrect(true)
        option.setQuestion(question)
        question.addOption(option)
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

        def quizAnswer = new QuizAnswer()
        quizAnswer.setAnswerDate(LocalDateTime.now())
        quizAnswer.setCompleted(true)
        quizAnswer.setUser(user)
        user.addQuizAnswer(quizAnswer)
        quizAnswer.setQuiz(quiz)
        quiz.addQuizAnswer(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizAnswer(quizAnswer)
        quizAnswer.addQuestionAnswer(questionAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        quizQuestion.addQuestionAnswer(questionAnswer)
        questionAnswer.setOption(option)
        option.addQuestionAnswer(questionAnswer)

        userRepository.save(user)
        quizRepository.save(quiz)
        questionRepository.save(question)
        quizAnswerRepository.save(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
    }

    def 'get solved quizzes for the student'() {
        when:
        def solvedQuizDtos = statementService.getSolvedQuizzes(USERNAME)

        then: 'returns correct data'
        solvedQuizDtos.size() == 1
        def solvedQuizDto = solvedQuizDtos.get(0)
        def statementQuizDto = solvedQuizDto.getStatementQuiz()
        statementQuizDto.getQuestions().size() == 1
        solvedQuizDto.getAnswers().size() == 1
        def answer = solvedQuizDto.getAnswers().get(0)
        answer.getQuizQuestionId() == quizQuestion.getId()
        answer.getOptionId() == option.getId()
        solvedQuizDto.getCorrectAnswers().size() == 1
        def correct = solvedQuizDto.getCorrectAnswers().get(0)
        correct.getQuizQuestionId() == quizQuestion.getId()
        correct.getCorrectOptionId() == option.getId()
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
