package pt.ulisboa.tecnico.socialsoftware.tutor.auth.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DemoAuthWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)
    }

    def "user confirms registration"() {
        when:
        def response = restClient.get(
                path: '/auth/demo/student',
                query: [
                        createNew: false,
                ],
                requestContentType: 'application/json'
        )

        then: "check response status"
        response.status == 200
        response.data.token != ""
        response.data.user.name == DEMO_STUDENT_NAME
        response.data.user.role == "STUDENT"
    }

}
