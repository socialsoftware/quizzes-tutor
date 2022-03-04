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
        when: "a weekly score is created"
        weeklyScoreService.createWeeklyScore(dashboard.getId())

        then: "the weekly score is inside the weekly score repository and with the correct data"
        weeklyScoreRepository.count() == 1L
        def result = weeklyScoreRepository.findAll().get(0)
        result.getId() != null
        result.getDashboard().getId() == dashboard.getId()
        result.getNumberAnswered() == 0
        result.getUniquelyAnswered() == 0
        result.getPercentageCorrect() == 0
        and: "dashboard contains the week score"
        def dashboard = dashboardRepository.getById(dashboard.getId())
        dashboard.getWeeklyScores().contains(result)
    }

    def "cannot create multiple WeeklyScore for the same week"() {
        given: "a week score"
        weeklyScoreService.createWeeklyScore(dashboard.getId())

        when: "when another is created for the same week"
        weeklyScoreService.createWeeklyScore(dashboard.getId())

        then: "WEEKLY_SCORE_ALREADY_CREATED exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.WEEKLY_SCORE_ALREADY_CREATED
        weeklyScoreRepository.count() == 1L
    }

    @Unroll
    def "#test - cannot create WeeklyScore with dashboard=#dashboardId"() {
        when: "a weekly score is created"
        weeklyScoreService.createWeeklyScore(dashboardId)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage
        weeklyScoreRepository.count() == 0L

        where:
        test                                              | dashboardId || errorMessage
        "create weekly score with null dashboard"         | null        || DASHBOARD_NOT_FOUND
        "create weekly score with non-existing dashboard" | 100         || DASHBOARD_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}