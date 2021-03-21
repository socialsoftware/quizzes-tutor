package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser

@DataJpaTest
class RemoveQuizTest extends SpockTest {
    def question
    def quiz
    def quizQuestion
    def user

    def setup() {
        createExternalCourseAndExecution()

        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)

        question = new Question()
        question.setKey(1)
        question.setCourse(externalCourse)
        question.setTitle("Question Title")
        question.setContent("Question Content")
        questionRepository.save(question)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz)

        quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(1)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)
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

    def "remove a quiz that has an answer"() {
        given: 'a quiz answer'
        def quizAnswer = new QuizAnswer()
        quizAnswer.setQuiz(quiz)
        quizAnswer.setUser(user)
        quizAnswerRepository.save(quizAnswer)

        when:
        quizService.removeQuiz(quiz.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUIZ_HAS_ANSWERS
        quizRepository.count() == 1L
        quizQuestionRepository.count() == 1L
        questionRepository.count() == 1L
        question.getQuizQuestions().size() == 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
