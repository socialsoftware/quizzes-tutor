package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import spock.lang.Unroll

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
        and:
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getWeeklyScores().contains(result)
    }

    def "cannot create multiple WeeklyScore for the same week"() {
        given:
        weeklyScoreService.createWeeklyScore(dashboard.getId())

        when:
        weeklyScoreService.createWeeklyScore(dashboard.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.WEEKLY_SCORE_ALREADY_CREATED
        weeklyScoreRepository.count() == 1L
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