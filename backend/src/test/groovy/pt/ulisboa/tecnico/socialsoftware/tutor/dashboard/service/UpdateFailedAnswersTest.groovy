package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll
import java.time.LocalDateTime


@DataJpaTest
class UpdateFailedAnswersTest extends SpockTest {

    def student
    def dashboard
    def quiz1
    def quizQuestion1
    def quiz2
    def quizQuestion2
    def optionKO

    def createQuiz(count) {
        def quiz = new Quiz()
        quiz.setKey(count)
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)
        return quiz
    }

    def createQuestion(count, quiz) {
        def question = new Question()
        question.setKey(count)
        question.setTitle("Question Title")
        question.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionRepository.save(question)

        def option = new Option()
        option.setContent("Option Content")
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestionDetails(questionDetails)
        optionRepository.save(option)
        optionKO = new Option()
        optionKO.setContent("Option Content")
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        def quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)
        return quizQuestion
    }

    def answerQuiz(answered, correct, completed, question, quiz) {
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(completed)
        quizAnswer.setCreationDate(LocalDateTime.now())
        quizAnswer.setAnswerDate(LocalDateTime.now())
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setTimeTaken(1)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(question)

        def answerDetails
        if (answered && correct) answerDetails = new MultipleChoiceAnswer(questionAnswer, optionRepository.findAll().get(0))
        else if (answered && !correct ) answerDetails = new MultipleChoiceAnswer(questionAnswer, optionRepository.findAll().get(1))
        else {
            questionAnswerRepository.save(questionAnswer)
            return questionAnswer
        }
        questionAnswer.setAnswerDetails(answerDetails)
        answerDetailsRepository.save(answerDetails)
        questionAnswerRepository.save(questionAnswer)
        return questionAnswer
    }

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)

        quiz1 = createQuiz(1)
        quizQuestion1 = createQuestion(1, quiz1)
    }

    def 'update a failed answer' () {
        given:
        def questionAnswer = answerQuiz(true, false, true, quizQuestion1, quiz1)

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId())

        then: "a failed answer is saved in the database"
        failedAnswerRepository.findAll().size() == 1L
        def failedAnswer = failedAnswerRepository.findAll().get(0)
        failedAnswer.getId() != 0
        failedAnswer.getAnswered()
        failedAnswer.getCollected() != null
        !failedAnswer.getRemoved()
        failedAnswer.getQuestionAnswer().id === questionAnswer.getId()
        failedAnswer.getDashboard().id === dashboard.getId()
        failedAnswer.getDashboard().getStudent().getId() === student.getId()

        and: "is updated in the student dashboard"
        def dashboard = dashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === student.getId()
        dashboard.getCourseExecution().getId() === externalCourseExecution.getId()
        dashboard.getFailedAnswers().findAll().size() == 1L
        dashboard.getFailedAnswers().findAll().get(0).getId() === failedAnswer.getId()
    }

    def 'update an unanswered failed answer' () {
        given:
        def questionAnswer = answerQuiz(false, false, true, quizQuestion1, quiz1)

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId())

        then: "a failed answer is saved in the database"
        failedAnswerRepository.findAll().size() == 1L
        def failedAnswer = failedAnswerRepository.findAll().get(0)
        failedAnswer.getId() != 0
        !failedAnswer.getAnswered()
        failedAnswer.getCollected() != null
        !failedAnswer.getRemoved()
        failedAnswer.getQuestionAnswer().id === questionAnswer.getId()
        failedAnswer.getDashboard().id === dashboard.getId()
        failedAnswer.getDashboard().getStudent().getId() === student.getId()

        and: "is updated in the student dashboard"
        def dashboard = dashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === student.getId()
        dashboard.getCourseExecution().getId() === externalCourseExecution.getId()
        dashboard.getFailedAnswers().findAll().size() == 1L
        dashboard.getFailedAnswers().findAll().get(0).getId() === failedAnswer.getId()
    }

    def 'update two failed answers from different quizzes' () {
        given:
        quiz2 = createQuiz(2)
        quizQuestion2 = createQuestion(2, quiz2)
        def questionAnswer1 = answerQuiz(true, false, true, quizQuestion1, quiz1)
        def questionAnswer2 = answerQuiz(false, false, true, quizQuestion2, quiz2)

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId())

        then: "a failed answer is saved in the database"
        failedAnswerRepository.findAll().size() == 2L
        def failedAnswer1 = failedAnswerRepository.findAll().get(0)
        failedAnswer1.getId() != 0
        failedAnswer1.getAnswered()
        failedAnswer1.getCollected() != null
        !failedAnswer1.getRemoved()
        failedAnswer1.getQuestionAnswer().id === questionAnswer1.getId()
        failedAnswer1.getDashboard().id === dashboard.getId()
        failedAnswer1.getDashboard().getStudent().getId() === student.getId()
        def failedAnswer2 = failedAnswerRepository.findAll().get(1)
        failedAnswer2.getId() != 0
        !failedAnswer2.getAnswered()
        failedAnswer2.getCollected() != null
        !failedAnswer2.getRemoved()
        failedAnswer2.getQuestionAnswer().id === questionAnswer2.getId()
        failedAnswer2.getDashboard().id === dashboard.getId()
        failedAnswer2.getDashboard().getStudent().getId() === student.getId()

        and: "is updated in the student dashboard"
        def dashboard = dashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === student.getId()
        dashboard.getCourseExecution().getId() === externalCourseExecution.getId()
        dashboard.getFailedAnswers().findAll().size() == 2L
        dashboard.getFailedAnswers().findAll().get(0).getId() === failedAnswer1.getId()
        dashboard.getFailedAnswers().findAll().get(1).getId() === failedAnswer2.getId()
    }

    def 'update empty failed answers for an answer that does not belong to a completed quiz' () {
        given:
        answerQuiz(false, false, false, quizQuestion1, quiz1)

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId())

        then: "no failed answer is saved in the database"
        failedAnswerRepository.findAll().size() == 0L

        and: "the student dashboard's failed answers is empty"
        def dashboard = dashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === student.getId()
        dashboard.getCourseExecution().getId() === externalCourseExecution.getId()
        dashboard.getFailedAnswers().findAll().size() == 0L
    }

    def 'update empty failed answers for a correct completed quiz' () {
        given:
        answerQuiz(false, true, false, quizQuestion1, quiz1)

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId())

        then: "no failed answer is saved in the database"
        failedAnswerRepository.findAll().size() == 0L

        and: "the student dashboard's failed answers is empty"
        def dashboard = dashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === student.getId()
        dashboard.getCourseExecution().getId() === externalCourseExecution.getId()
        dashboard.getFailedAnswers().findAll().size() == 0L
    }

    @Unroll
    def "cannot create a failed answer for invalid dashboard (#dashboardId)"() {
        when:
        failedAnswerService.updateFailedAnswers(dashboardId)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        dashboardId    || errorMessage
        -1           || ErrorMessage.DASHBOARD_NOT_FOUND
        100            || ErrorMessage.DASHBOARD_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}