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
        given: 'a rest client'
        restClient = new RESTClient("http://localhost:" + port)
        and: 'the demo course execution'
        courseExecutionDto = courseService.getDemoCourse()
    }

    def "demo student gets a new dashboard"() {
        given: 'a demon student'
        demoStudentLogin()

        when: 'the web service is invoked'
        response = restClient.get(
                path: '/students/dashboards/executions/' + courseExecutionDto.getCourseExecutionId(),
                requestContentType: 'application/json'
        )

        then: "the request returns 200"
        response.status == 200
        and: "has value"
        response.data.id != null
        and: 'it is in the database'
        dashboardRepository.findAll().size() == 1

        cleanup:
        dashboardRepository.deleteAll()
    }

    def "demo student gets existing dashboard"() {
        given: 'a demon student'
        demoStudentLogin()
        and: 'its dashboard'
        def student = authUserService.demoStudentAuth(false).getUser()
        def dashboardDto = dashboardService.createDashboard(courseExecutionDto.getCourseExecutionId(), student.getId())

        when: 'the web service is invoked'
        response = restClient.get(
                path: '/students/dashboards/executions/' + courseExecutionDto.getCourseExecutionId(),
                requestContentType: 'application/json'
        )

        then: "the request returns 200"
        response.status == 200
        and: "it is the same dashboard"
        response.data.id == dashboardDto.id
        and: 'it is in the database'
        dashboardRepository.findAll().size() == 1

        cleanup:
        dashboardRepository.deleteAll()
    }

    def "demo teacher does not have access"() {
        given: 'demo teacher'
        demoTeacherLogin()

        when: 'the web service is invoked'
        response = restClient.get(
                path: '/students/dashboards/executions/' + courseExecutionDto.getCourseExecutionId(),
                requestContentType: 'application/json'
        )

        then: "the server understands the request but refuses to authorize it"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        and: 'database is clean'
        dashboardRepository.findAll().size() == 0

        cleanup:
        dashboardRepository.deleteAll()
    }

}