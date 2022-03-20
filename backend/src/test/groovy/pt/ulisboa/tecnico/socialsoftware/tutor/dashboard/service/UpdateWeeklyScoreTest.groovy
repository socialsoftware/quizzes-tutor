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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DASHBOARD_NOT_FOUND

@DataJpaTest
class UpdateWeeklyScoreTest extends SpockTest {
    def student
    def dashboard
    def quiz
    def quizQuestion
    def optionOK
    def OptionKO
    def now

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY);
        LocalDate week = DateHandler.now().with(weekSunday).toLocalDate();
        now = week.atStartOfDay().plusMinutes(10)

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

        optionKO = new Option()
        optionKO.setContent(OPTION_1_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(0)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        quiz = new Quiz()
        quiz.setType(Quiz.QuizType.PROPOSED.name())
        quiz.setKey(1)
        quiz.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz)

        quizQuestion= new QuizQuestion()
        quizQuestion.setQuestion(question)
        quizQuestion.setQuiz(quiz)
        quizQuestionRepository.save(quizQuestion)
    }

    def "update weekly score"() {
        given:
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        quizAnswer.setCreationDate(now)
        quizAnswer.setAnswerDate(now)
        quizAnswerRepository.save(quizAnswer)
        and:
        def questionAnswer = new QuestionAnswer()
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, optionOK)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)

        when:
        weeklyScoreService.updateWeeklyScore(dashboard.getId())

        then:
        weeklyScoreRepository.count() == 1L
        def result = weeklyScoreRepository.findAll().get(0)
        result.getId() != null
        result.getDashboard().getId() == dashboard.getId()
        result.getNumberAnswered() == 1
        result.getUniquelyAnswered() == 1
        result.getPercentageCorrect() == 100
        !result.isClosed()
        result.getDashboard().getLastCheckWeeklyScores().isAfter(now)
    }

    def "update weekly score without answers in the current week"() {
        when:
        weeklyScoreService.updateWeeklyScore(dashboard.getId())

        then:
        weeklyScoreRepository.count() == 1L
        def result = weeklyScoreRepository.findAll().get(0)
        result.getId() != null
        result.getDashboard().getId() == dashboard.getId()
        result.getNumberAnswered() == 0
        result.getUniquelyAnswered() == 0
        result.getPercentageCorrect() == 0
        !result.isClosed()
        result.getDashboard().getLastCheckWeeklyScores().isAfter(now)
    }

    def "during update delete weekly score in a past week without answers and closed"() {
        given:
        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY);
        LocalDate week = now.minusDays(30).with(weekSunday).toLocalDate();
        def closedWeeklyScore = new WeeklyScore(dashboard, week)
        closedWeeklyScore.setClosed(true)

        when:
        weeklyScoreService.updateWeeklyScore(dashboard.getId())

        then:
        weeklyScoreRepository.count() == 1L
        def result = weeklyScoreRepository.findAll().get(0)
        result.getId() != null
        result.getId() != closedWeeklyScore.getId()
        result.getDashboard().getId() == dashboard.getId()
        result.getNumberAnswered() == 0
        result.getUniquelyAnswered() == 0
        result.getPercentageCorrect() == 0
        !result.isClosed()
        result.getDashboard().getLastCheckWeeklyScores().isAfter(now)
    }

    def "update weekly score with wrong answers in the current week"() {
        given:
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        quizAnswer.setCreationDate(now)
        quizAnswer.setAnswerDate(now)
        quizAnswerRepository.save(quizAnswer)
        and:
        def questionAnswer = new QuestionAnswer()
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, optionKO)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)

        when:
        weeklyScoreService.updateWeeklyScore(dashboard.getId())

        then:
        weeklyScoreRepository.count() == 1L
        def result = weeklyScoreRepository.findAll().get(0)
        result.getId() != null
        result.getDashboard().getId() == dashboard.getId()
        result.getNumberAnswered() == 1
        result.getUniquelyAnswered() == 1
        result.getPercentageCorrect() == 0
        !result.isClosed()
        result.getDashboard().getLastCheckWeeklyScores().isAfter(now)
    }

    def "update weekly score of a closed IN_CLASS quiz in the current week"() {
        given:
        def inClassQuiz = new Quiz()
        inClassQuiz.setType(Quiz.QuizType.IN_CLASS.name())
        inClassQuiz.setCourseExecution(externalCourseExecution)
        inClassQuiz.setAvailableDate(now.minusMinutes(5))
        inClassQuiz.setResultsDate(now)
        quizRepository.save(inClassQuiz)
        and:
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(inClassQuiz)
        quizAnswer.setCreationDate(now.minusMinutes(4))
        quizAnswer.setAnswerDate(now.minusMinutes(4))
        quizAnswerRepository.save(quizAnswer)
        and:
        def questionAnswer = new QuestionAnswer()
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, optionKO)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)

        when:
        weeklyScoreService.updateWeeklyScore(dashboard.getId())

        then:
        weeklyScoreRepository.count() == 1L
        def result = weeklyScoreRepository.findAll().get(0)
        result.getId() != null
        result.getDashboard().getId() == dashboard.getId()
        result.getNumberAnswered() == 1
        result.getUniquelyAnswered() == 1
        result.getPercentageCorrect() == 0
        !result.isClosed()
        result.getDashboard().getLastCheckWeeklyScores().isAfter(now)
    }

    def "update weekly score of a not closed IN_CLASS quiz in the current week"() {
        given:
        def inClassQuiz = new Quiz()
        inClassQuiz.setType(Quiz.QuizType.IN_CLASS.name())
        inClassQuiz.setCourseExecution(externalCourseExecution)
        inClassQuiz.setAvailableDate(now.minusMinutes(5))
        inClassQuiz.setResultsDate(DateHandler.now().plusMinutes(10))
        quizRepository.save(inClassQuiz)
        and:
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(inClassQuiz)
        quizAnswer.setCreationDate(now.minusMinutes(4))
        quizAnswer.setAnswerDate(now.minusMinutes(4))
        quizAnswerRepository.save(quizAnswer)
        and:
        def questionAnswer = new QuestionAnswer()
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, optionKO)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)

        when:
        weeklyScoreService.updateWeeklyScore(dashboard.getId())

        then:
        weeklyScoreRepository.count() == 1L
        def result = weeklyScoreRepository.findAll().get(0)
        result.getId() != null
        result.getDashboard().getId() == dashboard.getId()
        result.getNumberAnswered() == 0
        result.getUniquelyAnswered() == 0
        result.getPercentageCorrect() == 0
        !result.isClosed()
        result.getDashboard().getLastCheckWeeklyScores().isAfter(DateHandler.now().minusMinutes(1))
    }

    def "update weekly score of a closed IN_CLASS quiz in the week before"() {
        given:
        def inClassQuiz = new Quiz()
        inClassQuiz.setType(Quiz.QuizType.IN_CLASS.name())
        inClassQuiz.setCourseExecution(externalCourseExecution)
        inClassQuiz.setAvailableDate(now.minusDays(7).minusMinutes(5))
        inClassQuiz.setResultsDate(now)
        quizRepository.save(inClassQuiz)
        and:
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(inClassQuiz)
        quizAnswer.setCreationDate(now.minusDays(7).minusMinutes(4))
        quizAnswer.setAnswerDate(now.minusDays(7).minusMinutes(4))
        quizAnswerRepository.save(quizAnswer)
        and:
        def questionAnswer = new QuestionAnswer()
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, optionKO)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)

        when:
        weeklyScoreService.updateWeeklyScore(dashboard.getId())

        then:
        weeklyScoreRepository.count() == 2L
        def result1 = weeklyScoreRepository.findAll().get(0)
        result1.getId() != null
        result1.getDashboard().getId() == dashboard.getId()
        result1.getDashboard().getLastCheckWeeklyScores().isAfter(now)
        and:
        def result2 = weeklyScoreRepository.findAll().get(1)
        result2.getId() != null
        result2.getDashboard().getId() == dashboard.getId()
        result2.getDashboard().getLastCheckWeeklyScores().isAfter(now)
        and:
        result1.getNumberAnswered() + result2.getNumberAnswered() == 1
        result1.getUniquelyAnswered() + result2.getUniquelyAnswered() == 1
        result1.getPercentageCorrect() + result2.getPercentageCorrect() == 0
        result1.isClosed() || result2.isClosed()
    }

    def "update weekly score of a not closed IN_CLASS quiz in the week before"() {
        given:
        def inClassQuiz = new Quiz()
        inClassQuiz.setType(Quiz.QuizType.IN_CLASS.name())
        inClassQuiz.setCourseExecution(externalCourseExecution)
        inClassQuiz.setAvailableDate(now.minusDays(7).minusMinutes(5))
        inClassQuiz.setResultsDate(now.plusDays(7))
        quizRepository.save(inClassQuiz)
        and:
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(inClassQuiz)
        quizAnswer.setCreationDate(now.minusDays(7).minusMinutes(4))
        quizAnswer.setAnswerDate(now.minusDays(7).minusMinutes(4))
        quizAnswerRepository.save(quizAnswer)
        and:
        def questionAnswer = new QuestionAnswer()
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, optionKO)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)

        when:
        weeklyScoreService.updateWeeklyScore(dashboard.getId())

        then:
        weeklyScoreRepository.count() == 2L
        def result1 = weeklyScoreRepository.findAll().get(0)
        result1.getId() != null
        result1.getDashboard().getId() == dashboard.getId()
        result1.getDashboard().getLastCheckWeeklyScores().isAfter(now)
        and:
        def result2 = weeklyScoreRepository.findAll().get(1)
        result2.getId() != null
        result2.getDashboard().getId() == dashboard.getId()
        result2.getDashboard().getLastCheckWeeklyScores().isAfter(now)
        and:
        result1.getNumberAnswered() + result2.getNumberAnswered() == 0
        result1.getUniquelyAnswered() + result2.getUniquelyAnswered() == 0
        result1.getPercentageCorrect() + result2.getPercentageCorrect() == 0
        !result1.isClosed() && !result2.isClosed()
    }

    @Unroll
    def "cannot update WeeklyScore with dsahboard=#dashboardId"() {
        when:
        weeklyScoreService.updateWeeklyScore(dashboardId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == DASHBOARD_NOT_FOUND

        where:
        dashboardId << [null, 100]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}