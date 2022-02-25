package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import spock.lang.Unroll

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
        given:
        // create WeeklyScore successfully
        expect: true
    }

    def "cannot create multiple WeeklyScore for the same week"() {
        given:
        // attempt to create multiple WeeklyScore for same week and raise exception
        expect: true
    }

    @Unroll
    def "cannot create WeeklyScore with dashboard=#dashboardId"() {
        given:
        // attempt to create WeeklyScore with invalid dashboard and raise exception
        expect: true
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}