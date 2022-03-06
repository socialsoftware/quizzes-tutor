package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.webservice


import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MultipleChoiceStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service.FailedAnswersSpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RemoveFailedAnswersWebServiceIT extends FailedAnswersSpockTest {
    @LocalServerPort
    private int port

    def response
    def courseExecution


    def setup() {
        given: 'a rest client'
        restClient = new RESTClient("http://localhost:" + port)

        and: 'a demo course execution'
        courseExecution = courseService.getDemoCourse()

        and: 'a quiz with future conclusionDate'
        createQuizAndQuestionIT()

        and: 'a student login'
        demoStudentLogin()
        student = authUserService.demoStudentAuth(false).getUser()

        and: 'a dashboard'
        def dashboardDto = dashboardService.createDashboard(courseExecution.getCourseExecutionId(), student.getId())
    }

    def "student gets failed answers from dashboard then removes it"() {

        given: 'a failed answer to the quiz'
        def answer = answerQuizIT()

        when: 'the web service is invoked'
        response = restClient.delete(
                path: '/students/dashboards/executions/' + courseExecution.getCourseExecutionId() +'/failedanswer/remove/' + answer.getQuestionAnswerId(),
                requestContentType: 'application/json'
        )

        then: "the request returns 200"
        response != null
        response.status == 200

        and: 'it is not in the database'
        failedAnswerRepository.findAll().size() == 0

        and: 'it is not in the dashboard'
        def dashboard = dashboardRepository.findAll().get(0)
        dashboard.getAllFailedAnswers().isEmpty()

        cleanup:
        dashboardRepository.deleteAll()
        quizAnswerRepository.deleteAll()
        userRepository.deleteAll()
        failedAnswerRepository.deleteAll()
    }

    def "teacher can't get remove student's failed answers from dashboard"() {

        given: 'a failed answer to the quiz'
        def answer = answerQuizIT()

        and: 'a teacher login'
        demoTeacherLogin()

        when: 'the web service is invoked'
        response = restClient.get(
                path: '/students/dashboards/executions/' + courseExecution.getCourseExecutionId() + '/failedanswer/remove/' + answer.getQuestionAnswerId(),
                requestContentType: 'application/json'
        )

        then: "no permission to do the request"
        response != null
        response.status == 403

        cleanup:
        dashboardRepository.deleteAll()
        quizAnswerRepository.deleteAll()
        userRepository.deleteAll()
        failedAnswerRepository.deleteAll()
    }

    def "student can't get another student's failed answers from dashboard"() {

        given: 'a failed answer to the quiz'
        def answer = answerQuizIT()

        and: 'another student login'
        demoStudentLogin()
        def newStudent = authUserService.demoStudentAuth(true).getUser()

        and: 'the new students dashboard'
        def newDashboardDto = dashboardService.createDashboard(courseExecution.getCourseExecutionId(), newStudent.getId())

        when: 'the web service is invoked'
        response = restClient.get(
                path: '/students/dashboards/executions/' + courseExecution.getCourseExecutionId()+'/failedanswer/remove/' + answer.getQuestionAnswerId(),
                requestContentType: 'application/json'
        )

        then: "no permission to do the request"
        response != null
        response.status == 403

        cleanup:
        dashboardRepository.deleteAll()
        quizAnswerRepository.deleteAll()
        userRepository.deleteAll()
        failedAnswerRepository.deleteAll()
    }

    def cleanup(){
        failedAnswerRepository.deleteAll()
        dashboardRepository.deleteAll()
        quizAnswerRepository.deleteAll()
        userRepository.deleteAll()
        quizQuestionRepository.deleteAll()
        questionRepository.deleteAll()
        questionDetailsRepository.deleteAll()
        quizRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        courseRepository.deleteAll()
    }


}