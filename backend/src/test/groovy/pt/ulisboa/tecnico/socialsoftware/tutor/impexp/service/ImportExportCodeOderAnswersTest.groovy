package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeFillInAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeOrderAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeOrderAnswerSlot
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInSpot
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderSlot
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@DataJpaTest
class ImportExportCodeOderAnswersTest extends SpockTest {
    def quizAnswer
    def questionAnswer

    def setup() {
        createExternalCourseAndExecution()

        Question question = new Question()
        question.setCourse(externalCourse)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        def questionDetails = new CodeOrderQuestion()
        question.setQuestionDetails(questionDetails)

        CodeOrderSlot slot = new CodeOrderSlot()
        slot.setSequence(0)
        slot.setContent(OPTION_1_CONTENT)
        slot.setOrder(0)
        slot.setQuestionDetails(questionDetails)
        questionDetails.getCodeOrderSlots().add(slot)

        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        Quiz quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quiz.setCourseExecution(externalCourseExecution)

        quiz.setCreationDate(DateHandler.now())
        quiz.setAvailableDate(DateHandler.now())
        quiz.setConclusionDate(DateHandler.now())
        quiz.setType(Quiz.QuizType.EXAM.toString())
        quizRepository.save(quiz)

        QuizQuestion quizQuestion= new QuizQuestion()
        quizQuestion.setSequence(0)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)

        User user = userService.createUserWithAuth(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, AuthUser.Type.EXTERNAL).getUser()

        quizAnswer = new QuizAnswer(user, quiz)
        quizAnswer.setAnswerDate(LOCAL_DATE_TODAY)
        quizAnswer.setCompleted(true)
        quizAnswerRepository.save(quizAnswer)

        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 1, 0)
        def answer = new CodeOrderAnswer(questionAnswer)
        def answerSlot = new CodeOrderAnswerSlot(slot,answer, 0)
        answer.getOrderedSlots().add(answerSlot)
        questionAnswer.setAnswerDetails(answer)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answer)
    }

    def 'export and import answers'() {
        given: 'a xml with a quiz'
        def answersXml = answerService.exportAnswers()
        print(answersXml)
        and: 'delete answers'
        answerService.deleteQuizAnswer(quizAnswer)

        when:
        answerService.importAnswers(answersXml)

        then:
        quizAnswerRepository.findAll().size() == 1
        def quizAnswerResult = quizAnswerRepository.findAll().get(0)
        quizAnswerResult.getAnswerDate() == LOCAL_DATE_TODAY
        quizAnswerResult.isCompleted()
        quizAnswerResult.getUser().getName() == USER_1_NAME
        quizAnswerResult.getUser().getUsername() == USER_1_USERNAME
        quizAnswerResult.getQuiz().getKey() == 1
        questionAnswerRepository.findAll().size() == 1
        def questionAnswerResult = questionAnswerRepository.findAll().get(0)
        questionAnswerResult.getTimeTaken() == 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
