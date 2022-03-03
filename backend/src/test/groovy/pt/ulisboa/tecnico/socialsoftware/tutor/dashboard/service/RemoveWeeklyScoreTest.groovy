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
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.WeeklyScore
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

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.WEEKLY_SCORE_NOT_FOUND

@DataJpaTest
class RemoveWeeklyScoreTest extends SpockTest {

    def student
    def dashboard
    def quiz
    def quizQuestion
    def optionOK

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)

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

    def "remove weekly score with answers within its week"() {
        given: "a past weekly score"
        WeeklyScore weeklyScore = new WeeklyScore(dashboard)
        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY);
        LocalDate week = DateHandler.now().minusDays(30).with(weekSunday).toLocalDate();
        weeklyScore.setWeek(week)
        weeklyScoreRepository.save(weeklyScore)

        and: "a answer submitted in a past weekly score week"
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        quizAnswer.setAnswerDate(DateHandler.now().minusDays(30))
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, optionOK)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)

        when: "the weekly score is removed"
        weeklyScoreService.removeWeeklyScore(weeklyScore.getId())

        then: "the weekly score is not inside the weekly score repository"
        weeklyScoreRepository.count() == 0L
    }

    def "remove weekly score without answers whitin its week"() {
        given: "a past weekly score"
        WeeklyScore weeklyScore = new WeeklyScore(dashboard)
        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY);
        LocalDate week = DateHandler.now().minusDays(30).with(weekSunday).toLocalDate();
        weeklyScore.setWeek(week)
        weeklyScoreRepository.save(weeklyScore)

        when: "the weekly score is removed and no answer is submitted"
        weeklyScoreService.removeWeeklyScore(weeklyScore.getId())

        then: "the weekly score is not inside the weekly score repository"
        weeklyScoreRepository.count() == 0L
    }

    def "cannot remove current weekly score"() {
        given: "a current weekly score"
        WeeklyScore weeklyScore = new WeeklyScore(dashboard)
        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY);
        LocalDate week = DateHandler.now().with(weekSunday).toLocalDate();
        weeklyScore.setWeek(week)
        weeklyScoreRepository.save(weeklyScore)

        when: "try to remove the current weekly score"
        weeklyScoreService.removeWeeklyScore(weeklyScore.getId())

        then: "CANNOT_REMOVE_WEEKLY_SCORE exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CANNOT_REMOVE_WEEKLY_SCORE
    }

    @Unroll
    def "#test - cannot remove WeeklyScore with weeklyScore=#weeklyScoreId"() {
        when: "a weekly score is removed"
        weeklyScoreService.removeWeeklyScore(weeklyScoreId)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        test                                              | weeklyScoreId || errorMessage
        "remove weekly score with null id"         | null        || WEEKLY_SCORE_NOT_FOUND
        "remove weekly score with non-existing id" | 100         || WEEKLY_SCORE_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
