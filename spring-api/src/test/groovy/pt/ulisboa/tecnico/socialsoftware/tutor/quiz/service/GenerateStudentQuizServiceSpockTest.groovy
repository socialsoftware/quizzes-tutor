package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository
import spock.lang.Specification

import java.util.stream.Collectors

@DataJpaTest
class GenerateStudentQuizServiceSpockTest extends Specification {
    @Autowired
    QuizService quizService

    @Autowired
    QuizRepository quizRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    def user
    def questionOne
    def questionTwo

    def setup() {
        user = new User('name', 'username', User.Role.STUDENT, 1, 2019)
        questionOne = new Question()
        questionOne.setNumber(1)
        questionOne.setActive(true)
        questionTwo = new Question()
        questionTwo.setNumber(2)
        questionTwo.setActive(true)

        userRepository.save(user)
        questionRepository.save(questionOne)
        questionRepository.save(questionTwo)
    }

    def 'generate quiz for one question and there are two questions available'() {
        when:
        quizService.generateStudentQuiz(user, 1)

        then:
        quizRepository.count() == 1L
        def result = quizRepository.findAll().get(0)
        result.getQuizAnswers().size() == 1
        def resQuizAnswer = result.getQuizAnswers().stream().collect(Collectors.toList()).get(0)
        resQuizAnswer.getQuiz() == result
        resQuizAnswer.getUser() == user
        resQuizAnswer.getQuestionAnswers().size() == 0
        result.getQuizQuestions().size() == 1
        def resQuizQuestion = result.getQuizQuestions().stream().collect(Collectors.toList()).get(0)
        resQuizQuestion.getQuiz() == result
        resQuizQuestion.getQuestion() == questionOne || resQuizQuestion.getQuestion() == questionTwo
        (questionOne.getQuizQuestions().size() == 1 &&  questionTwo.getQuizQuestions().size() == 0) ||  (questionOne.getQuizQuestions().size() == 0 &&  questionTwo.getQuizQuestions().size() == 1)
        questionOne.getQuizQuestions().contains(resQuizQuestion) || questionTwo.getQuizQuestions().contains(resQuizQuestion)
    }

    def 'generate quiz for two question and there are two questions available'() {
        when:
        quizService.generateStudentQuiz(user, 2)

        then:
        quizRepository.count() == 1L
        def result = quizRepository.findAll().get(0)
        result.getQuizAnswers().size() == 1
        def resQuizAnswer = result.getQuizAnswers().stream().collect(Collectors.toList()).get(0)
        resQuizAnswer.getQuiz() == result
        resQuizAnswer.getUser() == user
        resQuizAnswer.getQuestionAnswers().size() == 0
        result.getQuizQuestions().size() == 2
        result.getQuizQuestions().stream().map{quizQuestion -> quizQuestion.getQuestion()}.allMatch{question -> question == questionOne || question == questionTwo}
        questionOne.getQuizQuestions().size() == 1
        questionTwo.getQuizQuestions().size() == 1
    }

    def 'generate quiz for three question and there are two questions available'() {
        when:
        quizService.generateStudentQuiz(user, 3)

        then:
        TutorException exception = thrown()
        exception.getError() == TutorException.ExceptionError.NOT_ENOUGH_QUESTIONS
        exception.getValue() == Integer.toString(2)
        quizRepository.count() == 0L
    }

    @TestConfiguration
    static class QuizServiceImplTestContextConfiguration {

        @Bean
        QuizService quizService() {
            return new QuizService()
        }
    }

}
