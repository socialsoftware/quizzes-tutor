package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.WeeklyScore
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
class GetWeeklyScoreTest extends SpockTest {
    def student
    def dashboard

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)
    }

    def "get ordered weekly scores"() {
        given:
        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)
        LocalDate week = DateHandler.now().with(weekSunday).toLocalDate()
        def weeklyScoreOne = new WeeklyScore(dashboard, week.minusDays(50))
        weeklyScoreRepository.save(weeklyScoreOne)
        and:
        WeeklyScore weeklyScoreTwo = new WeeklyScore(dashboard, week)
        weeklyScoreRepository.save(weeklyScoreTwo)

        when: "get weekly scores"
        def weeklyScoreDtos = weeklyScoreService.getWeeklyScores(dashboard.getId())

        then: "get 2 weekly scores ordered by date"
        weeklyScoreDtos.size() == 2
        def weeklyScoreDtoOne = weeklyScoreDtos.get(0)
        weeklyScoreDtoOne.getId() == weeklyScoreTwo.getId()
        weeklyScoreDtoOne.getNumberAnswered() == weeklyScoreTwo.getNumberAnswered()
        weeklyScoreDtoOne.getUniquelyAnswered() == weeklyScoreTwo.getUniquelyAnswered()
        weeklyScoreDtoOne.getPercentageCorrect() == weeklyScoreTwo.getPercentageCorrect()
        weeklyScoreDtoOne.getWeek() == DateHandler.toISOString(week.atStartOfDay())
        def weeklyScoreDtoTwo = weeklyScoreDtos.get(1)
        weeklyScoreDtoTwo.getId() == weeklyScoreOne.getId()
        weeklyScoreDtoTwo.getNumberAnswered() == weeklyScoreOne.getNumberAnswered()
        weeklyScoreDtoTwo.getUniquelyAnswered() == weeklyScoreOne.getUniquelyAnswered()
        weeklyScoreDtoTwo.getPercentageCorrect() == weeklyScoreOne.getPercentageCorrect()
        weeklyScoreDtoTwo.getWeek() == DateHandler.toISOString(week.minusDays(50).atStartOfDay())
    }

    @Unroll
    def "cannot get WeeklyScores with invalid dashboard=#dashboardId"() {
        when:
        weeklyScoreService.getWeeklyScores(dashboardId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        dashboardId  || errorMessage
        null         || DASHBOARD_NOT_FOUND
        100          || DASHBOARD_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
