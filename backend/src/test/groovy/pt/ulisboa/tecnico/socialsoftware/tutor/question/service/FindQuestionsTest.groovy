package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@DataJpaTest
class FindQuestionsTest extends SpockTest {
    def user

    def setup() {
        createExternalCourseAndExecution()

        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)

        user.addCourse(externalCourseExecution)
        userRepository.save(user)
    }

    def "create a question with image and two options and a quiz questions with two answers"() {
        given: "createQuestion a question"
        Question question = new Question()
        question.setKey(1)
        question.setTitle("Question Title")
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        question.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        and: 'an image'
        def image = new Image()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        imageRepository.save(image)
        question.setImage(image)

        and: 'two options'
        def optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)

        def optionKO = new Option()
        optionKO.setContent(OPTION_1_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(0)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        Quiz quiz = new Quiz()
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setKey(1)
        quiz.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz)

        QuizQuestion quizQuestion= new QuizQuestion()
        quizQuestion.setQuestion(question)
        quizQuestion.setQuiz(quiz)
        quizQuestionRepository.save(quizQuestion)

        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setUser(user)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, optionOK)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)

        questionAnswer = new QuestionAnswer()
        answerDetails = new MultipleChoiceAnswer(questionAnswer, optionKO)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)

        when:
        def result = questionService.findQuestions(externalCourse.getId())

        then: "the returned data are correct"
        result.size() == 1
        def resQuestion = result.get(0)
        resQuestion.getId() != null
        resQuestion.getStatus() == Question.Status.AVAILABLE.name()
        resQuestion.getContent() == QUESTION_1_CONTENT
        resQuestion.getNumberOfAnswers() == 2
        resQuestion.getNumberOfCorrect() == 1
        resQuestion.getDifficulty() == 50
        resQuestion.getImage().getId() != null
        resQuestion.getImage().getUrl() == IMAGE_1_URL
        resQuestion.getImage().getWidth() == 20
        resQuestion.getQuestionDetailsDto().getOptions().size() == 2
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
