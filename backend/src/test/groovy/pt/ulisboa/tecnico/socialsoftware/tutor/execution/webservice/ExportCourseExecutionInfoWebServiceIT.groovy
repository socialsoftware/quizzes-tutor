package pt.ulisboa.tecnico.socialsoftware.tutor.execution.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTestIT

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExportCourseExecutionInfoWebServiceIT extends SpockTestIT {
    @LocalServerPort
    private int port

    def courseExecutionDto

    def setup() {
        given:
        deleteAll()
        and:
        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        and: 'the demo course execution'
        courseExecutionDto = courseService.getDemoCourse()
    }

    def "teacher exports a course"() {
        given: 'a demon teacher'
        demoTeacherLogin()
        
        when: "the web service is invoked"
        webClient.get()
                .uri('/executions/' + courseExecutionDto.getCourseExecutionId() + '/export')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(Void.class)
                .block()

        then: "no exception"
        true
    }
}