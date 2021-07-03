package pt.ulisboa.tecnico.socialsoftware.apigateway.webservice.auth

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.apigateway.SpockTestIT
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetDemoAuthWebServiceIT extends SpockTestIT {

    @LocalServerPort
    private int port

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)
    }

    def "demo student login"() {
        when:
        def response = restClient.get(
                path: '/auth/demo/student',
                query: [
                        createNew: false,
                ],
        )

        then: "check response status"
        response.status == 200
        response.data.token != ""
        response.data.user.name == DEMO_STUDENT_NAME
        response.data.user.role == Role.STUDENT.toString()
    }

    def "demo admin login"() {
        when:
        def response = restClient.get(
                path: '/auth/demo/admin',
        )

        then: "check response status"
        response.status == 200
        response.data.token != ""
        response.data.user.name == DEMO_ADMIN_NAME
        response.data.user.role == Role.DEMO_ADMIN.toString()
    }

    def "demo teacher login"() {
        when:
        def response = restClient.get(
                path: '/auth/demo/teacher',
        )

        then: "check response status"
        response.status == 200
        response.data.token != ""
        response.data.user.name == DEMO_TEACHER_NAME
        response.data.user.role == Role.TEACHER.toString()
    }

}
