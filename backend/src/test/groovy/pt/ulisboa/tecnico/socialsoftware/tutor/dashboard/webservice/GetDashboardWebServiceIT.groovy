package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DemoUtils

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetDashboardWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response

    def courseExecutionDto

    def setup() {
        given:
        restClient = new RESTClient("http://localhost:" + port)
        and:
        courseExecutionDto = courseService.getDemoCourse()
    }

    def "demo student gets a new dashboard"() {
        given:
        demoStudentLogin()

        when:
        response = restClient.get(
                path: '/students/dashboards/executions/' + courseExecutionDto.getCourseExecutionId(),
                requestContentType: 'application/json'
        )

        then:
        response.status == 200
        and:
        response.data.id != null
        and:
        dashboardRepository.findAll().size() == 1

        cleanup:
        dashboardRepository.deleteAll()
    }

    def "demo student gets existing dashboard"() {
        given:
        demoStudentLogin()
        and:
        def student = authUserService.demoStudentAuth(false).getUser()
        def dashboardDto = dashboardService.createDashboard(courseExecutionDto.getCourseExecutionId(), student.getId())

        when:
        response = restClient.get(
                path: '/students/dashboards/executions/' + courseExecutionDto.getCourseExecutionId(),
                requestContentType: 'application/json'
        )

        then:
        response.status == 200
        and:
        response.data.id == dashboardDto.id
        and:
        dashboardRepository.findAll().size() == 1

        cleanup:
        dashboardRepository.deleteAll()
    }

    def "demo teacher does not have access"() {
        given:
        demoTeacherLogin()

        when:
        response = restClient.get(
                path: '/students/dashboards/executions/' + courseExecutionDto.getCourseExecutionId(),
                requestContentType: 'application/json'
        )

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        and:
        dashboardRepository.findAll().size() == 0

        cleanup:
        dashboardRepository.deleteAll()
    }

}