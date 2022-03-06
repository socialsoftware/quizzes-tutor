package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.webservice

import com.fasterxml.jackson.databind.ObjectMapper
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
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateFailedAnswersWebServiceIT extends FailedAnswersSpockTest {
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

    def "student removes failed answers from dashboard, then re-adds it"() {

        given: 'a failed answer to the quiz'
        def answer = answerQuizIT()

        and: 'the failed answer is removed'
        failedAnswerService.removeFailedAnswer(dashboardDto.getFailedAnswers().get(0))

        when: 'the web service is invoked'

        //TODO CHANGE TO POST WITH CORRECT DATA
        def mapper = new ObjectMapper()
        def stringJson = mapper.writeValueAsString(DateHandler.now().minusDays(1),DateHandler.now())

        response = restClient.post(
                path: '/students/dashboards/executions/' + courseExecution.getCourseExecutionId() + '/failedanswers/update/' + answer.getQuestionAnswerId(),
                body: stringJson,
                requestContentType: 'application/json'
        )

        then: "the request returns 200"
        response != null
        response.status == 200
        and: "has value"
        response.data.id != null

        and: 'it is still in the database'
        failedAnswerRepository.findAll().size() == 1

        and: 'it is the right failed answer'
        def fa = failedAnswerRepository.findAll().get(0)
        fa.getQuestionAnswer().getId() == answer.getQuestionAnswerId()

        and: 'it is in the dashboard'
        def dashboard = dashboardRepository.findAll().get(0)
        dashboard.getAllFailedAnswers().size() == 1
        dashboard.getAllFailedAnswers().get(0).getId() == fa.getId()

        cleanup:
        dashboardRepository.deleteAll()
        quizAnswerRepository.deleteAll()
        userRepository.deleteAll()
        failedAnswerRepository.deleteAll()
    }

    def "teacher cant re-add failed answers to a students dashboard"() {

        given: 'a failed answer to the quiz'
        def answer = answerQuizIT()

        and: 'the failed answer is removed'
        failedAnswerService.removeFailedAnswer(dashboardDto.getFailedAnswers().get(0))

        and: 'a teacher login'
        demoTeacherLogin()

        when: 'the web service is invoked'

        //TODO CHANGE TO POST WITH CORRECT DATA
        def mapper = new ObjectMapper()
        def stringJson = mapper.writeValueAsString(DateHandler.now().minusDays(1),DateHandler.now())

        response = restClient.post(
                path: '/students/dashboards/executions/' + courseExecution.getCourseExecutionId() + '/failedanswers/update/' + answer.getQuestionAnswerId(),
                body: stringJson,
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

    def "student cant re-add another students failed answers"() {

        given: 'a failed answer to the quiz'
        def answer = answerQuizIT()

        and: 'the failed answer is removed'
        failedAnswerService.removeFailedAnswer(dashboardDto.getFailedAnswers().get(0))

        and: 'another student login'
        demoStudentLogin()
        def newStudent = authUserService.demoStudentAuth(true).getUser()

        and: 'a new dashboard'
        def newDashboardDto = dashboardService.createDashboard(courseExecution.getCourseExecutionId(), newStudent.getId())

        when: 'the web service is invoked'

        //TODO CHANGE TO POST WITH CORRECT DATA
        def mapper = new ObjectMapper()
        def stringJson = mapper.writeValueAsString(DateHandler.now().minusDays(1),DateHandler.now())

        response = restClient.post(
                path: '/students/dashboards/executions/' + courseExecution.getCourseExecutionId() + '/failedanswers/update/' + answer.getQuestionAnswerId(),
                body: stringJson,
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