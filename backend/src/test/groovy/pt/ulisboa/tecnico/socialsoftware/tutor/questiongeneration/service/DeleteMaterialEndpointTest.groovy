package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.service

import com.sun.net.httpserver.HttpServer
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.QuestionGenerationController
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.QuestionGenerationService
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.WebClientAqgClient
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AQG_SERVICE_ERROR

/** The two thin layers of deleting a material: the HTTP client of the service and the controller. */
class DeleteMaterialEndpointTest extends Specification {
    HttpServer server
    def requests = []

    def cleanup() {
        server?.stop(0)
    }

    def serve(int status, String body = '') {
        server = HttpServer.create(new InetSocketAddress('localhost', 0), 0)
        server.createContext('/', { exchange ->
            requests << "${exchange.requestMethod} ${exchange.requestURI.path}".toString()
            byte[] bytes = body.getBytes('UTF-8')
            exchange.getResponseHeaders().add('Content-Type', 'application/json')
            exchange.sendResponseHeaders(status, status == 204 ? -1 : bytes.length)
            if (status != 204) exchange.getResponseBody().write(bytes)
            exchange.close()
        })
        server.start()
        return new WebClientAqgClient("http://localhost:${server.address.port}", '')
    }

    def "the client asks the service to forget the material"() {
        given:
        def client = serve(204)

        when:
        client.deleteMaterial('m1')

        then:
        requests == ['DELETE /materials/m1']
    }

    def "the reason the service gives for refusing is passed on"() {
        given:
        def client = serve(409, '{"detail":"the material is still being read; try again when it is done"}')

        when:
        client.deleteMaterial('m1')

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == AQG_SERVICE_ERROR
        exception.getMessage().contains('still being read')
    }

    def "the controller hands the deletion to the service"() {
        given:
        def service = Mock(QuestionGenerationService)
        def controller = new QuestionGenerationController()
        controller.@questionGenerationService = service

        when:
        controller.deleteMaterial(3, 'm1')

        then:
        1 * service.deleteMaterial(3, 'm1')
    }
}
