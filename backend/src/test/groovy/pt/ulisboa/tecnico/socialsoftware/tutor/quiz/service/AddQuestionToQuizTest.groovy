package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import spock.lang.Specification

@DataJpaTest
class AddQuestionToQuizTest extends Specification {
    @Autowired
    QuizService quizService

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    def setup() {
        def quiz = new Quiz()
        quiz.setKey(1)
        quizRepository.save(quiz)
        def question = new Question()
        question.setKey(1)
        question.setTitle("Question title")
        questionRepository.save(question)
    }

    def 'add a question to a quiz' () {
        given:
        def quizId = quizRepository.findAll().get(0).getId()
        def questionId = questionRepository.findAll().get(0).getId()

        when:
        quizService.addQuestionToQuiz(questionId, quizId)

        then:
        quizQuestionRepository.findAll().size() == 1
        def quizQuestion = quizQuestionRepository.findAll().get(0)
        quizQuestion.getId() != null
        quizQuestion.getSequence() == 0
        quizQuestion.getQuiz() != null
        quizQuestion.getQuiz().getQuizQuestions().size() == 1
        quizQuestion.getQuiz().getQuizQuestions().contains(quizQuestion)
        quizQuestion.getQuestion() != null
        quizQuestion.getQuestion().getQuizQuestions().size() == 1
        quizQuestion.getQuestion().getQuizQuestions().contains(quizQuestion)
    }

    @TestConfiguration
    static class QuizServiceImplTestContextConfiguration {

        @Bean
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        AnswersXmlImport xmlImporter() {
            return new AnswersXmlImport()
        }
    }
}
