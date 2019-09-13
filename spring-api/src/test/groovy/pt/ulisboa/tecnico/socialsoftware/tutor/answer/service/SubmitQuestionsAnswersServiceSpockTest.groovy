package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswersDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException.ExceptionError.QUIZ_MISMATCH
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException.ExceptionError.USER_MISMATCH

@DataJpaTest
class SubmitQuestionsAnswersServiceSpockTest extends Specification {
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
    def date

    def setup() {
        def quiz = new Quiz()
        quiz.setNumber(1)
        user = new User('name', 'username', User.Role.STUDENT, 1, 2019)
        quizAnswer = new QuizAnswer(user, quiz)
        def question = new Question()
        question.setNumber(1)
        quizQuestion = new QuizQuestion(quiz, question, 0)
        def optionError = new Option()
        optionError.setCorrect(false)
        question.addOption(optionError)
        optionOk = new Option()
        optionOk.setCorrect(true)
        question.addOption(optionOk)

        def date = LocalDateTime.now()

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
        def resultsDto = new ArrayList<ResultAnswerDto>()
        def resultDto = new ResultAnswerDto()
        resultDto.setQuizQuestionId(quizQuestion.getId())
        resultDto.setOptionId(optionOk.getId())
        resultsDto.add(resultDto)
        def resultAnswersDto = new ResultAnswersDto()
        resultAnswersDto.setQuizAnswerId(quizAnswer.getId())
        resultAnswersDto.setAnswerDate(date)
        resultAnswersDto.setAnswers(resultsDto)

        when:
        def correctAnswersDto = answerService.submitQuestionsAnswers(user, resultAnswersDto)

        then: 'the value is createQuestion and persistent'
        quizAnswer.getCompleted()
        quizAnswer.getAnswerDate() == date
        questionAnswerRepository.findAll().size() == 1
        def result = questionAnswerRepository.findAll().get(0)
        result.getQuizAnswer() == quizAnswer
        quizAnswer.getQuestionAnswers().contains(result)
        result.getQuizQuestion() == quizQuestion
        quizQuestion.getQuestionAnswers().contains(result)
        result.getOption() == optionOk
        optionOk.getQuestionAnswers().contains(result)
        and: 'the return value is OK'
        correctAnswersDto.getAnswers().size() == 1
        def correctAnswerDto = correctAnswersDto.getAnswers().get(0)
        correctAnswerDto.getQuizQuestionId() == quizQuestion.getId()
        correctAnswerDto.getCorrectOptionId() == optionOk.getId()
    }

    def 'user not consistent'() {
        given:
        def resultsDto = new ArrayList<ResultAnswerDto>()
        def resultDto = new ResultAnswerDto()
        resultDto.setQuizQuestionId(quizQuestion.getId())
        resultDto.setOptionId(optionOk.getId())
        resultsDto.add(resultDto)
        def resultAnswersDto = new ResultAnswersDto()
        resultAnswersDto.setQuizAnswerId(quizAnswer.getId())
        resultAnswersDto.setAnswers(resultsDto)
        and: 'another user'
        def otherUser = new User('name', 'username2', User.Role.STUDENT, 2, 2019)
        userRepository.save(otherUser)


        when:
        answerService.submitQuestionsAnswers(otherUser, resultAnswersDto)

        then:
        TutorException exception = thrown()
        exception.getError() == USER_MISMATCH
        exception.getValue() == 'username2'
        questionAnswerRepository.findAll().size() == 0
    }

    def 'quiz question not consistent'() {
        given:
        def resultsDto = new ArrayList<ResultAnswerDto>()
        def resultDto = new ResultAnswerDto()
        def quiz = new Quiz()
        quiz.setNumber(2)
        def question = new Question()
        question.setNumber(2)
        def quizQuestion = new QuizQuestion(quiz, question, 0)
        quizRepository.save(quiz)
        questionRepository.save(question)
        quizQuestionRepository.save(quizQuestion)
        resultDto.setQuizQuestionId(quizQuestion.getId())
        resultDto.setOptionId(optionOk.getId())
        resultsDto.add(resultDto)
        def resultAnswersDto = new ResultAnswersDto()
        resultAnswersDto.setQuizAnswerId(quizAnswer.getId())
        resultAnswersDto.setAnswers(resultsDto)

        when:
        answerService.submitQuestionsAnswers(user, resultAnswersDto)

        then:
        TutorException exception = thrown()
        exception.getError() == QUIZ_MISMATCH
        exception.getValue() == quizQuestion.getId().toString()
        questionAnswerRepository.findAll().size() == 0
    }

    def 'question option not consistent'() {
        given:
        def resultsDto = new ArrayList<ResultAnswerDto>()
        def resultDto = new ResultAnswerDto()
        resultDto.setQuizQuestionId(quizQuestion.getId())
        def otherOptionOK = new Option()
        otherOptionOK.setCorrect(true)
        def otherQuestion = new Question()
        otherQuestion.setNumber(2)
        otherQuestion.addOption(otherOptionOK)
        questionRepository.save(otherQuestion)
        optionRepository.save(otherOptionOK)
        resultDto.setOptionId(otherOptionOK.getId())
        resultsDto.add(resultDto)
        def resultAnswersDto = new ResultAnswersDto()
        resultAnswersDto.setQuizAnswerId(quizAnswer.getId())
        resultAnswersDto.setAnswers(resultsDto)

        when:
        answerService.submitQuestionsAnswers(user, resultAnswersDto)

        then:
        TutorException exception = thrown()
        exception.getError() == QUIZ_MISMATCH
        exception.getValue() == otherOptionOK.getId().toString()
        questionAnswerRepository.findAll().size() == 0
    }

    @TestConfiguration
    static class AnswerServiceImplTestContextConfiguration {

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }
    }
}
