package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTestIT
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DashboardDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetDashboardWebServiceIT extends SpockTestIT {
    @LocalServerPort
    private int port

    def response

    def courseExecutionDto

    def setup() {
        given:
        deleteAll()
        and:
        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        and:
        courseExecutionDto = courseService.getDemoCourse()
    }

    def "demo student gets a new dashboard"() {
        given:
        demoStudentLogin()

        when:
        def result = webClient.get()
                .uri('/students/dashboards/executions/' + courseExecutionDto.getCourseExecutionId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(DashboardDto.class)
                .block()

        then:
        result.id != null
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
        def result = webClient.get()
                .uri('/students/dashboards/executions/' + courseExecutionDto.getCourseExecutionId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(DashboardDto.class)
                .block()

        then:
        result.id == dashboardDto.id
        and:
        dashboardRepository.findAll().size() == 1

        cleanup:
        dashboardRepository.deleteAll()
    }

    def "demo teacher does not have access"() {
        given:
        demoTeacherLogin()

        when:
        def result = webClient.get()
                .uri('/students/dashboards/executions/' + courseExecutionDto.getCourseExecutionId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(DashboardDto.class)
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        and:
        dashboardRepository.findAll().size() == 0

        cleanup:
        dashboardRepository.deleteAll()
    }

}