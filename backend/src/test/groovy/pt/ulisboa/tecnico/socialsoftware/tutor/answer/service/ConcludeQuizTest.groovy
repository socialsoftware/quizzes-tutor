package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_NOT_YET_AVAILABLE

@DataJpaTest
class ConcludeQuizTest extends SpockTest {

    def user
    def quizQuestion
    def optionOk
    def optionKO
    def quizAnswer
    def date
    def quiz

    def setup() {
        user = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        user.addCourse(courseExecution)
        userRepository.save(user)
        user.setKey(user.getId())

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quiz.setCourseExecution(courseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)

        Question question = new Question()
        question.setKey(1)
        question.setTitle("Question Title")
        question.setCourse(course)
        questionRepository.save(question)

        quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)

        optionKO = new Option()
        optionKO.setContent("Option Content")
        optionKO.setCorrect(false)
        optionKO.setSequence(0)
        optionKO.setQuestion(question)
        optionRepository.save(optionKO)

        optionOk = new Option()
        optionOk.setContent("Option Content")
        optionOk.setCorrect(true)
        optionOk.setSequence(1)
        optionOk.setQuestion(question)
        optionRepository.save(optionOk)

        date = DateHandler.now()

        quizAnswer = new QuizAnswer(user, quiz)
        quizAnswerRepository.save(quizAnswer)
    }

    def 'conclude quiz without conclusionDate, without answering'() {
        when:
        def correctAnswers = answerService.concludeQuiz(user, quiz.getId())

        then: 'the value is createQuestion and persistent'
        quizAnswer.isCompleted()
        quizAnswer.getAnswerDate() != null
        questionAnswerRepository.findAll().size() == 1
        def questionAnswer = questionAnswerRepository.findAll().get(0)
        questionAnswer.getQuizAnswer() == quizAnswer
        quizAnswer.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getQuizQuestion() == quizQuestion
        quizQuestion.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getOption() == null
        and: 'the return value is OK'
        correctAnswers.size() == 1
        def correctAnswerDto = correctAnswers.get(0)
        correctAnswerDto.getSequence() == 0
        correctAnswerDto.getCorrectOptionId() == optionOk.getId()
    }

    def 'conclude quiz IN_CLASS without answering, before conclusionDate'() {
        given: 'an IN_CLASS quiz with future conclusionDate'
        quiz.setConclusionDate(DateHandler.now().plusDays(2))
        quiz.setType(Quiz.QuizType.IN_CLASS.toString())

        when:
        def correctAnswers = answerService.concludeQuiz(user, quiz.getId())

        then: 'the value is createQuestion and persistent'
        quizAnswer.isCompleted()
        quizAnswer.getAnswerDate() != null
        questionAnswerRepository.findAll().size() == 1
        def questionAnswer = questionAnswerRepository.findAll().get(0)
        questionAnswer.getQuizAnswer() == quizAnswer
        quizAnswer.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getQuizQuestion() == quizQuestion
        quizQuestion.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getOption() == null
        and: 'does not return answers'
        correctAnswers == []
    }

    def 'conclude quiz with answer, before conclusionDate'() {
        given: 'a quiz with future conclusionDate'
        quiz.setConclusionDate(DateHandler.now().plusDays(2))
        and: 'an answer'
        def statementAnswerDto = new StatementAnswerDto()
        statementAnswerDto.setOptionId(optionOk.getId())
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        answerService.submitAnswer(user, quiz.getId(), statementAnswerDto)

        when:
        def correctAnswers = answerService.concludeQuiz(user, quiz.getId())

        then: 'the value is createQuestion and persistent'
        quizAnswer.isCompleted()
        questionAnswerRepository.findAll().size() == 1
        def questionAnswer = questionAnswerRepository.findAll().get(0)
        questionAnswer.getQuizAnswer() == quizAnswer
        quizAnswer.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getQuizQuestion() == quizQuestion
        quizQuestion.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getOption() == optionOk
        optionOk.getQuestionAnswers().contains(questionAnswer)
        and: 'the return value is OK'
        correctAnswers.size() == 1
        def correctAnswerDto = correctAnswers.get(0)
        correctAnswerDto.getSequence() == 0
        correctAnswerDto.getCorrectOptionId() == optionOk.getId()
    }

    def 'conclude quiz without answering, before availableDate'() {
        given: 'a quiz with future availableDate'
        quiz.setAvailableDate(DateHandler.now().plusDays(2))

        when:
        answerService.concludeQuiz(user, quiz.getId())

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == QUIZ_NOT_YET_AVAILABLE
    }

    def 'user not consistent'() {
        given: 'another user'
        def otherUser = new User(USER_2_USERNAME, USER_2_USERNAME, User.Role.STUDENT)
        user.addCourse(courseExecution)
        userRepository.save(otherUser)
        otherUser.setKey(otherUser.getId())

        when:
        answerService.concludeQuiz(otherUser, quiz.getId())

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == QUIZ_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
