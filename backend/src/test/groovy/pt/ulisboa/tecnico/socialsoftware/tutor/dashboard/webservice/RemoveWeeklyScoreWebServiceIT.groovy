package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.WeeklyScore
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service.FailedAnswersSpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RemoveWeeklyScoreWebServiceIT extends FailedAnswersSpockTest {
    @LocalServerPort
    private int port

    def response
    def courseExecution
    def weeklyScore

    def setup() {
        given:
        restClient = new RESTClient("http://localhost:" + port)
        and:
        createExternalCourseAndExecution()
        and:
        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        and:
        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)
        and:
        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY);
        LocalDate week = DateHandler.now().minusDays(30).with(weekSunday).toLocalDate();
        weeklyScore = new WeeklyScore(dashboard, week)
        weeklyScoreRepository.save(weeklyScore)
    }

    def "student removes weekly score"() {
        given:
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        when:
        response = restClient.delete(
                path: '/students/weeklyscores/' + weeklyScore.getId(),
                requestContentType: 'application/json'
        )

        then:
        response != null
        response.status == 200
        and:
        weeklyScoreRepository.findAll().size() == 0
    }

    def "teacher can't get remove student's weekly score from dashboard"() {
        given: 'a teacher login'
        demoTeacherLogin()

        when:
        response = restClient.delete(
                path: '/students/weeklyscores/' + weeklyScore.getId(),
                requestContentType: 'application/json'
        )

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
    }

    def "student can't get another student's weekly score from dashboard"() {
        given:
        def newStudent = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.EXTERNAL)
        newStudent.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        userRepository.save(newStudent)
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        when:
        response = restClient.delete(
                path: '/students/weeklyscores/' + weeklyScore.getId(),
                requestContentType: 'application/json'
        )

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
    }

    def cleanup(){
        weeklyScoreRepository.deleteAll()
        dashboardRepository.deleteAll()
        userRepository.deleteAll()
        courseRepository.deleteAll()
    }
}