package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service.FailedAnswersSpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateFailedAnswersWebServiceIT extends FailedAnswersSpockTest {
    @LocalServerPort
    private int port

    def response
    def quiz
    def quizQuestion

    def setup() {
        given: 'a rest client'
        restClient = new RESTClient("http://localhost:" + port)
        and: 'a demo course execution'
        createExternalCourseAndExecution()
        and: 'a student'
        student = new Student(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        and: 'a dashboard'
        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)
        and: 'a failed answer to the quiz'
        quiz = createQuiz(1)
        quizQuestion = createQuestion(1, quiz)
        answerQuizIT(true, false, quiz)
    }

    def "student updates failed answers"() {
        given: 'a student login'
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        when: 'the web service is invoked'
        response = restClient.get(
                path: '/students/dashboards/' + dashboard.getId() + '/failedanswers/update',
                requestContentType: 'application/json'
        )

        then: "the request returns 200"
        response != null
        response.status == 200
        and: "has value"
        failedAnswerRepository.findAll().size() == 1

        and: 'it is in the database'
        failedAnswerRepository.findAll().size() == 1
    }

    def "student updates failed answers in specific time period"() {
        given: 'a student login'
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
        and: 'a wrong quiz answer'
        def quiz2 = createQuiz(2)
        quizQuestion = createQuestion(2, quiz2)
        answerQuizIT(true, false, quiz2)

        when: 'the web service is invoked'
        response = restClient.get(
                path: '/students/dashboards/' + dashboard.getId() + '/failedanswers/update',
                query: ['startDate': STRING_DATE_BEFORE, 'endDate': STRING_DATE_LATER],
                requestContentType: 'application/json'
        )

        then: "the request returns 200"
        response != null
        response.status == 200
        and: 'the two failed answers are in the database'
        failedAnswerRepository.findAll().size() == 2
    }

    def "teacher cant update student's failed answers"() {
        given: 'a teacher login'
        demoTeacherLogin()

        when: 'the web service is invoked'
        response = restClient.get(
                path: '/students/dashboards/' + dashboard.getId() + '/failedanswers/update',
                requestContentType: 'application/json'
        )

        then: "the server understands the request but refuses to authorize it"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
    }

    def "student cant re-add another students failed answers"() {
        given: 'another student'
        def newStudent = new Student(USER_2_NAME, USER_2_EMAIL, USER_2_EMAIL, false, AuthUser.Type.EXTERNAL)
        newStudent.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        userRepository.save(newStudent)
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        when: 'the web service is invoked'
        response = restClient.get(
                path: '/students/dashboards/' + dashboard.getId() + '/failedanswers/update',
                requestContentType: 'application/json'
        )

        then: "the server understands the request but refuses to authorize it"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
    }

    def cleanup(){
        dashboardRepository.deleteAll()
        userRepository.deleteAll()
        courseRepository.deleteAll()
    }
}