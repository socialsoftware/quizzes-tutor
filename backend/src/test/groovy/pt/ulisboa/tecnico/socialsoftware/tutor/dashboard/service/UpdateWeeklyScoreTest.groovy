package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.WeeklyScore
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DASHBOARD_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.WEEKLY_SCORE_NOT_FOUND

@DataJpaTest
class UpdateWeeklyScoreTest extends SpockTest {

    def student
    def dashboard
    def weeklyScore
    def quiz
    def quizQuestion
    def optionOK
    def OptionKO

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)

        weeklyScore = new WeeklyScore(dashboard)
        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY);
        LocalDate week = DateHandler.now().with(weekSunday).toLocalDate();
        weeklyScore.setWeek(week)
        weeklyScoreRepository.save(weeklyScore)

        Question question = new Question()
        question.setKey(1)
        question.setTitle("Question Title")
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)

        optionKO = new Option()
        optionKO.setContent(OPTION_1_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(0)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        quiz = new Quiz()
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setKey(1)
        quiz.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz)

        quizQuestion= new QuizQuestion()
        quizQuestion.setQuestion(question)
        quizQuestion.setQuiz(quiz)
        quizQuestionRepository.save(quizQuestion)
    }

    def "update weekly score"() {
        given: "a correct question answer"
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        quizAnswer.setAnswerDate(DateHandler.now())
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, optionOK)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)

        when: "a answer is submitted"
        weeklyScoreService.updateWeeklyScore(weeklyScore.getId())

        then: "the weekly score is inside the weekly score repository and with the correct data"
        weeklyScoreRepository.count() == 1L
        def result = weeklyScoreRepository.findAll().get(0)
        result.getId() != null
        result.getDashboard().getId() == dashboard.getId()
        result.getNumberAnswered() == 1
        result.getUniquelyAnswered() == 1
        result.getPercentageCorrect() == 100
    }

    def "update weekly score without answers in the current week"() {
        when: "no answer is submitted"
        weeklyScoreService.updateWeeklyScore(weeklyScore.getId())

        then: "the weekly score is inside the weekly score repository and with the correct data"
        weeklyScoreRepository.count() == 1L
        def result = weeklyScoreRepository.findAll().get(0)
        result.getId() != null
        result.getDashboard().getId() == dashboard.getId()
        result.getNumberAnswered() == 0
        result.getUniquelyAnswered() == 0
        result.getPercentageCorrect() == 0
    }

    def "update weekly score with wrong answers in the current week"() {
        given: "a wrong question answer"
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        quizAnswer.setAnswerDate(DateHandler.now())
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, optionKO)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)

        when: "a answer is submitted"
        weeklyScoreService.updateWeeklyScore(weeklyScore.getId())

        then: "the weekly score is inside the weekly score repository and with the correct data"
        weeklyScoreRepository.count() == 1L
        def result = weeklyScoreRepository.findAll().get(0)
        result.getId() != null
        result.getDashboard().getId() == dashboard.getId()
        result.getNumberAnswered() == 1
        result.getUniquelyAnswered() == 1
        result.getPercentageCorrect() == 0
    }

    def "cannot update old WeeklyScore"() {
        given: "old a weekly score"
        WeeklyScore oldWeeklyScore = new WeeklyScore(dashboard)
        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)
        LocalDate week = DateHandler.now().minusDays(20).with(weekSunday).toLocalDate()
        oldWeeklyScore.setWeek(week)
        weeklyScoreRepository.save(oldWeeklyScore)

        when:
        weeklyScoreService.updateWeeklyScore(oldWeeklyScore.getId())

        then: "UPDATE_WEEKLY_SCORE_NOT_POSSIBLE exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.UPDATE_WEEKLY_SCORE_NOT_POSSIBLE
    }

    @Unroll
    def "#test - cannot update WeeklyScore with weeklyScore=#weeklyScoreId"() {
        when: "a weekly score is updated"
        weeklyScoreService.updateWeeklyScore(weeklyScoreId)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        test                                              | weeklyScoreId || errorMessage
        "update weekly score with null id"         | null        || WEEKLY_SCORE_NOT_FOUND
        "update weekly score with non-existing id" | 100         || WEEKLY_SCORE_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
