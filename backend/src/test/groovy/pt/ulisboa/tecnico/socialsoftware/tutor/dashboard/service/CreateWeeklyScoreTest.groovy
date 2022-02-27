package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.WeeklyScoreService
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.WeeklyScoreRepository
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

    def "create WeeklyScore"() {
        when:
        weeklyScoreService.createWeeklyScore(dashboard)

        then: "the weekly score is inside the weekly score repository and with the correct data"
        weeklyScoreRepository.count() == 1L
        def result = weeklyScoreRepository.findAll().get(0)
        result.getId() != null
        result.getDashboard().getId() == dashboard.getId()
        result.getNumberAnswered() == 0
        result.getUniquelyAnswered() == 0
        result.getPercentageCorrect() == 0
    }

    def "cannot create multiple WeeklyScore for the same week"() {
        when:
        weeklyScoreService.createWeeklyScore(dashboard)
        weeklyScoreService.createWeeklyScore(dashboard)

        // attempt to create multiple WeeklyScore for same week and raise exception
        then: "WEEKLY_SCORE_ALREADY_CREATED exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.WEEKLY_SCORE_ALREADY_CREATED
        weeklyScoreRepository.count() == 1L
    }

    @Unroll
    def "#test: cannot create WeeklyScore with dashboard=#testDashboard"() {
        when:
        weeklyScoreService.createWeeklyScore(testDashboard)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage
        weeklyScoreRepository.count() == 0L

        where:
        test                                            | testDashboard         | errorMessage
        "create weekly score with empty dashboard"      | new Dashboard()       | DASHBOARD_NOT_FOUND
        "create weekly score with null dashboard"       | null                  | DASHBOARD_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}