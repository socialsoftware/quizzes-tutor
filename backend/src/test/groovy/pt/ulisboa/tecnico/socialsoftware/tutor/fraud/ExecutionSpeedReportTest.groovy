package pt.ulisboa.tecnico.socialsoftware.tutor.fraud

import com.sun.net.httpserver.HttpServer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import spock.lang.Specification

/** The report of a course asks the fraud service for it, after the answers of the quizzes given in class are written. */
class ExecutionSpeedReportTest extends Specification {
    HttpServer server
    def requests = []

    def cleanup() {
        server?.stop(0)
    }

    def fraudService(String json) {
        server = HttpServer.create(new InetSocketAddress('localhost', 0), 0)
        server.createContext('/', { exchange ->
            requests << "${exchange.requestMethod} ${exchange.requestURI.path} key=${exchange.requestHeaders.getFirst('X-API-Key')}".toString()
            byte[] bytes = json.getBytes('UTF-8')
            exchange.getResponseHeaders().add('Content-Type', 'application/json')
            exchange.sendResponseHeaders(200, bytes.length)
            exchange.getResponseBody().write(bytes)
            exchange.close()
        })
        server.start()
        return "http://localhost:${server.address.port}"
    }

    def quiz(Integer id, String type) {
        return Stub(QuizDto) {
            getId() >> id
            getType() >> type
        }
    }

    def "writes the answers of the quizzes given in class, asks the fraud service and passes on its report"() {
        given:
        def json = '''{"quizzesAnalysed": 3, "users": [{"userInfo": {"username": "ana", "name": "Ana", "user_id": "5"},
            "quizzesAnalysed": 3, "flaggedQuizzes": 2, "totalPoints": 5, "blockedOpenings": 1,
            "quizzes": [{"quizId": 9, "title": "Quiz 9", "answered": 5, "tooFast": 3, "meanSeconds": 12.5, "points": 3,
                         "level": "HIGH", "reasons": ["almost the same time on every question"], "extra": true}]}]}'''
        def controller = new FraudController()
        def quizService = Mock(QuizService)
        def answerService = Mock(AnswerService)
        controller.@quizService = quizService
        controller.@answerService = answerService
        controller.@fraudServiceURL = fraudService(json)
        controller.@fraudServiceApiKey = 'secret'
        quizService.findNonGeneratedQuizzes(4) >> [quiz(1, 'IN_CLASS'), quiz(2, 'PROPOSED'), quiz(3, 'IN_CLASS')]

        when:
        def report = controller.getExecutionSpeedFraudScores(4)

        then:
        1 * answerService.writeQuizAnswers(1)
        1 * answerService.writeQuizAnswers(3)
        0 * answerService.writeQuizAnswers(2)
        requests == ['GET /speed/execution/4 key=secret']
        report.quizzesAnalysed() == 3
        report.users().size() == 1
        def student = report.users()[0]
        student.userInfo().getUsername() == 'ana'
        student.flaggedQuizzes() == 2
        student.blockedOpenings() == 1
        student.quizzes()[0].level() == 'HIGH'
        student.quizzes()[0].reasons() == ['almost the same time on every question']
    }
}
