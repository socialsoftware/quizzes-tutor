package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import spock.lang.Specification

@DataJpaTest
class RemoveQuizServiceSpockTest extends Specification {
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String URL = 'URL'

    @Autowired
    QuizService quizService

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    def question
    def quiz
    def quizQuestion

    def setup() {
        given: "create a question"
        question = new Question()
        question.setNumber(1)
        and: 'a quiz and quiz question'
        quiz = new Quiz()
        quiz.setNumber(1)
        quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(1)

        quiz.addQuizQuestion(quizQuestion)
        quizQuestion.setQuiz(quiz)
        question.addQuizQuestion(quizQuestion)
        quizQuestion.setQuestion(question)

        quizRepository.save(quiz)
        questionRepository.save(question)
    }

    def "remove a quiz"() {
        when:
        quizService.removeQuiz(quiz.getId())

        then: "the quiz is removed"
        quizRepository.count() == 0L
        quizQuestionRepository.count() == 0L
        questionRepository.count() == 1L
        question.getQuizQuestions().size() == 0


    }

    def "remove a qiz that has an answer"() {
        given: 'a quiz answer'
        def quizAnswer = new QuizAnswer()
        quizAnswer.setQuiz(quiz)
        quiz.addQuizAnswer(quizAnswer)
        quizAnswerRepository.save(quizAnswer)

        when:
        quizService.removeQuiz(quiz.getId())

        then:
        def exception = thrown(TutorException)
        exception.getError() == ExceptionError.QUIZ_HAS_ANSWERS
        quizRepository.count() == 1L
        quizQuestionRepository.count() == 1L
        questionRepository.count() == 1L
        question.getQuizQuestions().size() == 1
    }

    @TestConfiguration
    static class TestContextConfiguration {

        @Bean
        QuizService quizService() {
            return new QuizService()
        }
    }

}
