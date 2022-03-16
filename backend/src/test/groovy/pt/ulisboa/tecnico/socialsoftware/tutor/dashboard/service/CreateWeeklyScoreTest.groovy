package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.WeeklyScore
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DASHBOARD_NOT_FOUND

@DataJpaTest
class CreateWeeklyScoreTest extends SpockTest {

    def student
    def dashboard

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, false)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)
    }

    def "create weekly score"() {
        when:
        weeklyScoreService.createWeeklyScore(dashboard.getId())

        then:
        weeklyScoreRepository.count() == 1L
        def result = weeklyScoreRepository.findAll().get(0)
        result.getId() != null
        result.getDashboard().getId() == dashboard.getId()
        result.getNumberAnswered() == 0
        result.getUniquelyAnswered() == 0
        result.getPercentageCorrect() == 0
        result.getSamePercentage().getWeeklyScores().size() == 0
        and:
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getWeeklyScores().contains(result)
        and:
        samePercentageRepository.findAll().size() == 1
    }

    def "create three weekly scores with same percentage"() {
        given:
        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)
        LocalDate week = DateHandler.now().with(weekSunday).toLocalDate()
        def weeklyScore1 = new WeeklyScore(dashboard, week.minusDays(50))
        weeklyScoreRepository.save(weeklyScore1)
        def weeklyScore2 = new WeeklyScore(dashboard, week.minusDays(30))
        weeklyScoreRepository.save(weeklyScore2)

        when:
        def weeklyScoreDto = weeklyScoreService.createWeeklyScore(dashboard.getId())

        then:
        weeklyScoreRepository.count() == 3L
        def result1 = weeklyScoreRepository.findById(weeklyScore1.getId()).get()
        result1.getId() == weeklyScore1.getId()
        result1.getDashboard().getId() == dashboard.getId()
        result1.getNumberAnswered() == 0
        result1.getUniquelyAnswered() == 0
        result1.getPercentageCorrect() == 0
        result1.getSamePercentage().weeklyScores.size() == 2
        result1.getSamePercentage().weeklyScores.contains(weeklyScore2)
        def result2 = weeklyScoreRepository.findById(weeklyScore2.getId()).get()
        result2.getId() == weeklyScore2.getId()
        result2.getDashboard().getId() == dashboard.getId()
        result2.getNumberAnswered() == 0
        result2.getUniquelyAnswered() == 0
        result2.getPercentageCorrect() == 0
        result2.getSamePercentage().weeklyScores.size() == 2
        result2.getSamePercentage().weeklyScores.contains(weeklyScore1)
        def result3 = weeklyScoreRepository.findById(weeklyScoreDto.getId()).get()
        result3.getId() == weeklyScoreDto.getId()
        result3.getDashboard().getId() == dashboard.getId()
        result3.getNumberAnswered() == 0
        result3.getUniquelyAnswered() == 0
        result3.getPercentageCorrect() == 0
        result3.getSamePercentage().weeklyScores.size() == 2
        result3.getSamePercentage().weeklyScores.contains(weeklyScore1)
        result3.getSamePercentage().weeklyScores.contains(weeklyScore2)
        and:
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getWeeklyScores().contains(result3)
        and:
        samePercentageRepository.findAll().size() == 3
        def samePercentage1 = samePercentageRepository.findAll().get(0)
        samePercentage1.getWeeklyScores().size() == 2
        def samePercentage2 = samePercentageRepository.findAll().get(1)
        samePercentage2.getWeeklyScores().size() == 2
        def samePercentage3 = samePercentageRepository.findAll().get(2)
        samePercentage3.getWeeklyScores().size() == 2
    }

    def "create two weekly scores with different percentage"() {
        given:
        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)
        LocalDate week = DateHandler.now().with(weekSunday).toLocalDate()
        def weeklyScore = new WeeklyScore(dashboard, week.minusDays(50))
        weeklyScore.setPercentageCorrect(30)
        weeklyScoreRepository.save(weeklyScore)

        when:
        def weeklyScoreDto = weeklyScoreService.createWeeklyScore(dashboard.getId())

        then:
        weeklyScoreRepository.count() == 2L
        def result = weeklyScoreRepository.findById(weeklyScore.getId()).get()
        result.getId() == weeklyScore.getId()
        result.getDashboard().getId() == dashboard.getId()
        result.getNumberAnswered() == 0
        result.getUniquelyAnswered() == 0
        result.getPercentageCorrect() == 30
        result.getSamePercentage().weeklyScores.size() == 0
        def result2 = weeklyScoreRepository.findById(weeklyScoreDto.getId()).get()
        result2.getId() == weeklyScoreDto.getId()
        result2.getDashboard().getId() == dashboard.getId()
        result2.getNumberAnswered() == 0
        result2.getUniquelyAnswered() == 0
        result2.getPercentageCorrect() == 0
        result2.getSamePercentage().weeklyScores.size() == 0
        and:
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getWeeklyScores().contains(result)
        and:
        samePercentageRepository.findAll().size() == 2
        def samePercentage1 = samePercentageRepository.findAll().get(0)
        samePercentage1.getWeeklyScores().size() == 0
        def samePercentage2 = samePercentageRepository.findAll().get(1)
        samePercentage2.getWeeklyScores().size() == 0
    }

    def "cannot create multiple WeeklyScore for the same week"() {
        given:
        weeklyScoreService.createWeeklyScore(dashboard.getId())

        when:
        weeklyScoreService.createWeeklyScore(dashboard.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.WEEKLY_SCORE_ALREADY_CREATED
        weeklyScoreRepository.count() == 1
        samePercentageRepository.findAll().size() == 1
    }

    @Unroll
    def "cannot create WeeklyScore with invalid dashboard=#dashboardId"() {
        when:
        weeklyScoreService.createWeeklyScore(dashboardId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage
        weeklyScoreRepository.count() == 0L

        where:
        dashboardId || errorMessage
        null        || DASHBOARD_NOT_FOUND
        100         || DASHBOARD_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}