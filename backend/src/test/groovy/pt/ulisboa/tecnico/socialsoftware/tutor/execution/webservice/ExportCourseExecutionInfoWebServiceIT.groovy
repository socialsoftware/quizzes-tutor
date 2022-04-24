package pt.ulisboa.tecnico.socialsoftware.tutor.execution.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
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
        restClient = new RESTClient("http://localhost:" + port)
        and: 'the demo course execution'
        courseExecutionDto = courseService.getDemoCourse()
    }

    def "teacher exports a course"() {
        given: 'a demon teacher'
        demoTeacherLogin()
        and: 'prepare request response'
        restClient.handler.failure = { resp, reader ->
            [response: resp, reader: reader]
        }
        restClient.handler.success = { resp, reader ->
            [response: resp, reader: reader]
        }

        when: "the web service is invoked"
        def map = restClient.get(
                path: "/executions/" + courseExecutionDto.getCourseExecutionId() + "/export",
                requestContentType: "application/json"
        )

        then: "the response status is OK"
        assert map['response'].status == 200
        assert map['reader'] != null
    }
}