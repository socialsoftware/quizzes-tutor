package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DifficultQuestion
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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DASHBOARD_NOT_FOUND

@DataJpaTest
class UpdateDifficultQuestionsTest extends SpockTest {
    def student
    def dashboard
    def question
    def optionOK
    def optionKO
    def quiz
    def quizQuestion
    def now

    def setup() {
        given:
        createExternalCourseAndExecution()
        and:
        student = new Student(USER_1_NAME, USER_1_EMAIL, USER_1_PASSWORD, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        and:
        now = DateHandler.now()
        and:
        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        question.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)
        and:
        optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)
        and:
        optionKO = new Option()
        optionKO.setContent(OPTION_1_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)
        and:
        quiz = new Quiz()
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setAvailableDate(now.minusHours(1))
        quiz.setConclusionDate(now)
        quizRepository.save(quiz)
        and:
        quizQuestion = new QuizQuestion()
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)
        and:
        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)
    }

    def "create one difficult question that does not exist"() {
        given:
        def quizAnswer = new QuizAnswer()
        quizAnswer.setAnswerDate(now.minusMinutes(1))
        quizAnswer.setQuiz(quiz)
        quizAnswer.setStudent(student)
        quizAnswerRepository.save(quizAnswer)
        and:
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)

        when:
        difficultQuestionService.updateDifficultQuestions(dashboard.getId())

        then:
        difficultQuestionRepository.count() == 1L
        and:
        def difficultQuestion = difficultQuestionRepository.findAll().get(0)
        difficultQuestion.getDashboard() == dashboard
        difficultQuestion.getQuestion() == question
        difficultQuestion.isRemoved() == false
        difficultQuestion.getRemovedDate() == null
        difficultQuestion.getPercentage() == 0
    }

    def "delete and create a difficult question that continues to be difficult"() {
        given:
        def difficultQuestion = new DifficultQuestion(dashboard, question, 24)
        difficultQuestionRepository.save(difficultQuestion)
        and:
        def quizAnswer = new QuizAnswer()
        quizAnswer.setAnswerDate(now.minusMinutes(1))
        quizAnswer.setQuiz(quiz)
        quizAnswer.setStudent(student)
        quizAnswerRepository.save(quizAnswer)
        and:
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)

        when:
        difficultQuestionService.updateDifficultQuestions(dashboard.getId())

        then:
        difficultQuestionRepository.count() == 1L
        and:
        def result = difficultQuestionRepository.findAll().get(0)
        result.getId() != difficultQuestion.getId()
        result.getQuestion() == question
        result.getPercentage() == 0
    }

    def "delete difficult question that is not difficult anymore"() {
        given:
        def difficultQuestion = new DifficultQuestion(dashboard, question, 24)
        difficultQuestionRepository.save(difficultQuestion)

        when:
        difficultQuestionService.updateDifficultQuestions(dashboard.getId())

        then:
        difficultQuestionRepository.count() == 0L
    }

    @Unroll
    def "does not delete removed difficult question that was removed in less than #daysAgo days ago"() {
        given:
        def difficultQuestion = new DifficultQuestion(dashboard, question, 24)
        difficultQuestion.setRemovedDate(now.minusDays(daysAgo))
        difficultQuestion.setRemoved(true)
        difficultQuestionRepository.save(difficultQuestion)

        when:
        difficultQuestionService.updateDifficultQuestions(dashboard.getId())

        then:
        difficultQuestionRepository.count() == 1

        where:
        daysAgo << [0, 5, 6]
    }

    def "does not delete removed difficult question that was removed in less than 4 days ago even if it continues to be difficulty"() {
        given:
        def difficultQuestion = new DifficultQuestion(dashboard, question, 24)
        difficultQuestion.setRemovedDate(now.minusDays(4))
        difficultQuestion.setRemoved(true)
        difficultQuestionRepository.save(difficultQuestion)
        and:
        def quizAnswer = new QuizAnswer()
        quizAnswer.setAnswerDate(now.minusMinutes(1))
        quizAnswer.setQuiz(quiz)
        quizAnswer.setStudent(student)
        quizAnswerRepository.save(quizAnswer)
        and:
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)

        when:
        difficultQuestionService.updateDifficultQuestions(dashboard.getId())

        then:
        difficultQuestionRepository.count() == 1
        and:
        def result = difficultQuestionRepository.findAll().get(0)
        result.getId() == difficultQuestion.getId()
        result.getQuestion() == question
        result.getPercentage() == 24
    }

    @Unroll
    def "does not create difficult question of removed difficult question in less than 4 days ago"() {
        given:
        def difficultQuestion = new DifficultQuestion(dashboard, question, 24)
        difficultQuestion.setRemovedDate(now.minusDays(4))
        difficultQuestion.setRemoved(true)
        difficultQuestionRepository.save(difficultQuestion)
        and:
        def quizAnswer = new QuizAnswer()
        quizAnswer.setAnswerDate(now.minusMinutes(1))
        quizAnswer.setQuiz(quiz)
        quizAnswer.setStudent(student)
        quizAnswerRepository.save(quizAnswer)
        and:
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)

        when:
        difficultQuestionService.updateDifficultQuestions(dashboard.getId())

        then:
        difficultQuestionRepository.count() == 1
        and:
        def result = difficultQuestionRepository.findAll().get(0)
        result.getId() == difficultQuestion.getId()
        result.getQuestion() == question
        result.isRemoved() == true
        result.getRemovedDate() == now.minusDays(4)
        result.getPercentage() == 24
    }

    @Unroll
    def "delete removed difficult question that was removed in more than #daysAgo days ago"() {
        given:
        def difficultQuestion = new DifficultQuestion(dashboard, question, 24)
        difficultQuestion.setRemovedDate(now.minusDays(daysAgo))
        difficultQuestion.setRemoved(true)
        difficultQuestionRepository.save(difficultQuestion)

        when:
        difficultQuestionService.updateDifficultQuestions(dashboard.getId())

        then:
        difficultQuestionRepository.count() == 0L

        where:
        daysAgo << [7, 15]
    }

    @Unroll
    def "create difficult question that continues to be difficult of a removed difficult question that was removed in more than #daysAgo days ago"() {
        given:
        def difficultQuestion = new DifficultQuestion(dashboard, question, 24)
        difficultQuestion.setRemovedDate(now.minusDays(15))
        difficultQuestion.setRemoved(true)
        difficultQuestionRepository.save(difficultQuestion)
        and:
        def quizAnswer = new QuizAnswer()
        quizAnswer.setAnswerDate(now.minusMinutes(1))
        quizAnswer.setQuiz(quiz)
        quizAnswer.setStudent(student)
        quizAnswerRepository.save(quizAnswer)
        and:
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)

        when:
        difficultQuestionService.updateDifficultQuestions(dashboard.getId())

        then:
        difficultQuestionRepository.count() == 1L
        and:
        def result = difficultQuestionRepository.findAll().get(0)
        result.getId() != difficultQuestion.getId()
        result.getQuestion() == question
        result.getPercentage() == 0
    }

    @Unroll
    def "question is correctly computed as not difficulty with #numberOfIncorrect incorrect"() {
        given:
        answerQuiz(true)
        (1..numberOfIncorrect).each {
            answerQuiz(false)
        }

        when:
        difficultQuestionService.updateDifficultQuestions(dashboard.getId())

        then:
        difficultQuestionRepository.count() == 0L

        where:
        numberOfIncorrect << [0, 1, 3]
    }

    @Unroll
    def "question is correctly computed as difficult"() {
        given:
        answerQuiz(false)
        answerQuiz(false)
        answerQuiz(false)
        answerQuiz(false)
        answerQuiz(true)

        when:
        difficultQuestionService.updateDifficultQuestions(dashboard.getId())

        then:
        difficultQuestionRepository.count() == 1L
        and:
        def result = difficultQuestionRepository.findAll().get(0)
        result.getQuestion() == question
        result.getPercentage() == 20
    }

    @Unroll
    def "cannot update difficult questions with invalid dashboardId=#dashboardId"() {
        when:
        difficultQuestionService.updateDifficultQuestions(dashboardId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == DASHBOARD_NOT_FOUND
        difficultQuestionRepository.count() == 0L

        where:
        dashboardId << [0, 100]
    }

    def answerQuiz(correct, date = LocalDateTime.now()) {
        def quiz = new Quiz()
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setAvailableDate(now.minusHours(1))
        quiz.setConclusionDate(now)
        quizRepository.save(quiz)

        def quizQuestion = new QuizQuestion()
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)

        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setCreationDate(date)
        quizAnswer.setAnswerDate(date)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setTimeTaken(1)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)

        def answerDetails
        if (correct) answerDetails = new MultipleChoiceAnswer(questionAnswer, optionOK)
        else if (!correct) answerDetails = new MultipleChoiceAnswer(questionAnswer, optionKO)
        else {
            questionAnswerRepository.save(questionAnswer)
            return questionAnswer
        }
        questionAnswer.setAnswerDetails(answerDetails)
        answerDetailsRepository.save(answerDetails)
        return questionAnswer
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
