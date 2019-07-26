package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswersDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class CreateQuestionAnswerServiceMethodSpockTest extends Specification {
    @Autowired
    AnswerService answerService

    @Autowired
    UserRepository userRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    def user
    def quizQuestion
    def optionOk
    def quizAnswer

    def setup() {
        def quiz = new Quiz()
        user = new User('name', 'username', User.Role.STUDENT)
        quizAnswer = new QuizAnswer(user, quiz, LocalDateTime.now())
        def question = new Question()
        quizQuestion = new QuizQuestion(quiz, question, 0)
        def optionError = new Option()
        optionError.setCorrect(false)
        question.addOption(optionError)
        optionOk = new Option()
        optionOk.setCorrect(true)
        question.addOption(optionOk)

        userRepository.save(user)
        quizRepository.save(quiz)
        questionRepository.save(question)
        quizQuestionRepository.save(quizQuestion)
        quizAnswerRepository.save(quizAnswer)
        optionRepository.save(optionOk)
        optionRepository.save(optionError)
    }

    def 'create for one question and two options'() {
        given:
        def resutlsDto = new HashMap<Integer, ResultAnswerDto>()
        def resultDto = new ResultAnswerDto()
        resultDto.setQuizQuestionId(quizQuestion.getId())
        resultDto.setOptionId(optionOk.getId())
        resutlsDto.put(0, resultDto)
        def resultAnswersDto = new ResultAnswersDto()
        resultAnswersDto.setQuizAnswerId(quizAnswer.getId())
        resultAnswersDto.setAnswers(resutlsDto)

        when:
        answerService.createQuestionAnswer(user, resultAnswersDto)

        then:
        questionAnswerRepository.findAll().size() == 1
    }

    @TestConfiguration
    static class AnswerServiceImplTestContextConfiguration {

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }
    }
}
