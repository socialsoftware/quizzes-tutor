package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class QuestionAnswerPerformanceTest extends SpockTest {

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

        def question = new Question()
        question.setKey(1)
        question.setTitle("Question Title")
        question.setCourse(course)
        questionRepository.save(question)

        optionKO = new Option()
        optionKO.setContent("Option Content")
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestion(question)
        optionRepository.save(optionKO)

        optionKO = new Option()
        optionKO.setContent("Option Content")
        optionKO.setCorrect(false)
        optionKO.setSequence(2)
        optionKO.setQuestion(question)
        optionRepository.save(optionKO)

        optionKO = new Option()
        optionKO.setContent("Option Content")
        optionKO.setCorrect(false)
        optionKO.setSequence(3)
        optionKO.setQuestion(question)
        optionRepository.save(optionKO)

        optionOk = new Option()
        optionOk.setContent("Option Content")
        optionOk.setCorrect(true)
        optionOk.setSequence(4)
        optionOk.setQuestion(question)
        optionRepository.save(optionOk)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quiz.setCourseExecution(courseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)

        1.upto(1, {
            quizQuestion = new QuizQuestion(quiz, question, 0)
            quizQuestionRepository.save(quizQuestion)
        })

        date = DateHandler.now()

        quizAnswer = new QuizAnswer(user, quiz)
        quizAnswerRepository.save(quizAnswer)
    }

    def 'answer question performance'() {
        given: 'a set of empty question answers'
        def questionAnswers = quizAnswer.getQuestionAnswers()

        when:
        questionAnswers.eachWithIndex {
            questionAnswer , index ->
            def statementAnswerDto = new StatementAnswerDto()
            statementAnswerDto.setTimeTaken(index)
            statementAnswerDto.setSequence(questionAnswer.getSequence())
            statementAnswerDto.setQuestionAnswerId(questionAnswer.getId())
            statementAnswerDto.setOptionId(questionAnswer.getQuizQuestion().getQuestion().getOptions().get(0).getId())
            answerService.submitAnswer(user, statementAnswerDto)
        }

        then: 'the value is createQuestion and persistent'
        true
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}