package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateWeeklyScoreWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response

    def authUserDto
    def courseExecutionDto
    def dashboardDto

    def setup() {
        given:
        restClient = new RESTClient("http://localhost:" + port)
        and:
        courseExecutionDto = courseService.getDemoCourse()
        authUserDto = authUserService.demoStudentAuth(false).getUser()
        dashboardDto = dashboardService.getDashboard(courseExecutionDto.getCourseExecutionId(), authUserDto.getId())
    }

    def "demo student gets its weekly scores"() {
        given:
        demoStudentLogin()

        when:
        response = restClient.put(
                path: '/students/dashboards/' + dashboardDto.getId() + '/weeklyscores',
                requestContentType: 'application/json'
        )

        then:
        response.status == 200
        and:
        weeklyScoreRepository.findAll().size() == 1

        cleanup:
        weeklyScoreRepository.deleteAll()
        dashboardRepository.deleteAll()
    }

    def "demo teacher does not have access"() {
        given:
        demoTeacherLogin()

        when:
        response = restClient.put(
                path: '/students/dashboards/' + dashboardDto.getId() + '/weeklyscores',
                requestContentType: 'application/json'
        )

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        and:
        weeklyScoreRepository.findAll().size() == 0

        cleanup:
        weeklyScoreRepository.deleteAll()
        dashboardRepository.deleteAll()
    }

    def "student cant update another students failed answers"() {
        given:
        demoStudentLogin(true)

        when:
        response = restClient.put(
                path: '/students/dashboards/' + dashboardDto.getId() + '/weeklyscores',
                requestContentType: 'application/json'
        )

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        and:
        weeklyScoreRepository.findAll().size() == 0

        cleanup:
        weeklyScoreRepository.deleteAll()
        dashboardRepository.deleteAll()
    }

}