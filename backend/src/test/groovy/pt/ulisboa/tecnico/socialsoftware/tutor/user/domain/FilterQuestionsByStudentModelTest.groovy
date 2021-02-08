package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion

@DataJpaTest
class FilterQuestionsByStudentModelTest extends SpockTest {
    def availableQuestions
    def user
    def quiz
    def questionOne
    def questionTwo
    def quizQuestionOne
    def quizQuestionTwo
    def quizQuestionThree
    def quizQuestionFour
    def quizQuestionFive
    def quizQuestionSix

    def setup() {
        questionOne = new Question()
        questionTwo = new Question()
        def questionThree = new Question()
        def questionFour = new Question()
        def questionFive = new Question()
        def questionSix = new Question()
        quiz = new Quiz()
        quiz.setKey(1)
        quizQuestionOne = new QuizQuestion(quiz, questionOne, 0)
        quizQuestionTwo = new QuizQuestion(quiz, questionTwo, 1)
        quizQuestionThree = new QuizQuestion(quiz, questionThree, 2)
        quizQuestionFour = new QuizQuestion(quiz, questionFour, 3)
        quizQuestionFive = new QuizQuestion(quiz, questionFive, 4)
        quizQuestionSix = new QuizQuestion(quiz, questionSix, 5)

        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        userRepository.save(user)

        availableQuestions = [questionOne, questionTwo, questionThree, questionFour, questionFive, questionSix]
    }

    def 'the user did not answer any question' () {
        when:
        def result = user.filterQuestionsByStudentModel(5, availableQuestions)

        then:
        result.size() == 5
    }

    def 'the user answered 1 question' () {
        given:
        def quizAnswer = new QuizAnswer(user, quiz)
        Option option = new Option()
        option.setContent("Option Content")
        option.setCorrect(true)
        option.setSequence(0)
        def questionAnswer = new QuestionAnswer(quizAnswer, quizQuestionOne, 10, 0)
        questionAnswer.setAnswerDetails(new MultipleChoiceAnswer(questionAnswer, option))

        when:
        def result = user.filterQuestionsByStudentModel(5, availableQuestions)

        then:
        result.size() == 5
        and: 'it contains the answered question'
        result.contains(questionOne)
    }

    def 'the user answered 4 questions' () {
        given:
        def quizAnswer = new QuizAnswer(user, quiz)
        Option option = new Option()
        option.setContent("Option Content")
        option.setCorrect(true)
        option.setSequence(0)
        def questionAnswer = new QuestionAnswer(quizAnswer, quizQuestionThree, 10, 0)
        questionAnswer.setAnswerDetails(new MultipleChoiceAnswer(questionAnswer, option))

        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestionFour, 10, 0)
        questionAnswer.setAnswerDetails(new MultipleChoiceAnswer(questionAnswer, option))

        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestionFive, 10, 0)
        questionAnswer.setAnswerDetails(new MultipleChoiceAnswer(questionAnswer, option))

        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestionSix, 10, 0)
        questionAnswer.setAnswerDetails(new MultipleChoiceAnswer(questionAnswer, option))

        when:
        def result = user.filterQuestionsByStudentModel(5, availableQuestions)

        then:
        result.size() == 5
        and: 'it contains both not answered questions'
        result.contains(questionOne)
        result.contains(questionTwo)
    }

    def 'the user answered 5 questions' () {
        given:
        def quizAnswer = new QuizAnswer(user, quiz)
        Option option = new Option()
        option.setContent("Option Content")
        option.setCorrect(true)
        option.setSequence(0)
        def questionAnswer = new QuestionAnswer(quizAnswer, quizQuestionTwo, 10, 0)
        questionAnswer.setAnswerDetails(new MultipleChoiceAnswer(questionAnswer, option))

        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestionThree, 10, 0)
        questionAnswer.setAnswerDetails(new MultipleChoiceAnswer(questionAnswer, option))

        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestionFour, 10, 0)
        questionAnswer.setAnswerDetails(new MultipleChoiceAnswer(questionAnswer, option))

        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestionFive, 10, 0)
        questionAnswer.setAnswerDetails(new MultipleChoiceAnswer(questionAnswer, option))

        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestionSix, 10, 0)
        questionAnswer.setAnswerDetails(new MultipleChoiceAnswer(questionAnswer, option))


        when:
        def result = user.filterQuestionsByStudentModel(5, availableQuestions)

        then:
        result.size() == 5
        and: 'it contains the not answered question'
        result.contains(questionOne)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
