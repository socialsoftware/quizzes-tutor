package pt.ulisboa.tecnico.socialsoftware.tutor.auth.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTestIT
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto.AuthDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetDemoAuthWebServiceIT extends SpockTestIT {

    @LocalServerPort
    private int port

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
    }

    def "demo student login"() {
        when:
        def result = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path('/auth/demo/student')
                        .queryParam('createNew', false)
                        .build())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(AuthDto.class)
                .block()

        then: "check result status"
        result.token != ""
        result.user.name == DEMO_STUDENT_NAME
        result.user.role == User.Role.STUDENT
    }

    def "demo admin login"() {
        when:
        def result = webClient.get()
                .uri('/auth/demo/admin')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(AuthDto.class)
                .block()

        then: "check response status"
        result.token != ""
        result.user.name == DEMO_ADMIN_NAME
        result.user.role == User.Role.DEMO_ADMIN
    }

    def "demo teacher login"() {
        when:
        def result = webClient.get()
                .uri('/auth/demo/teacher')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(AuthDto.class)
                .block()

        then: "check response status"
        result.token != ""
        result.user.name == DEMO_TEACHER_NAME
        result.user.role == User.Role.TEACHER
    }

}
