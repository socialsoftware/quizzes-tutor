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
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RemoveFailedAnswersWebServiceIT extends FailedAnswersSpockTest {
    @LocalServerPort
    private int port

    def response
    def courseExecution
    def quiz
    def quizQuestion
    def failedAnswer

    def setup() {
        given:
        deleteAll()
        and:
        restClient = new RESTClient("http://localhost:" + port)
        and:
        createExternalCourseAndExecution()
        and:
        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        and:
        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)
        and:
        def question = createQuestion()
        quiz = createQuiz()
        quizQuestion = createQuizQuestion(quiz, question)
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz)
        failedAnswer = createFailedAnswer(questionAnswer, DateHandler.now().minusDays(8))
    }

    def "student gets failed answers from dashboard then removes it"() {
        given:
        externalUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        when:
        response = restClient.delete(
                path: '/students/failedanswers/' + failedAnswer.getId(),
                requestContentType: 'application/json'
        )

        then:
        response != null
        response.status == 200

        and:
        failedAnswerRepository.findAll().size() == 0
    }

    def "teacher can't get remove student's failed answers from dashboard"() {
        given:
        demoTeacherLogin()

        when:
        response = restClient.delete(
                path: '/students/failedanswers/' + failedAnswer.getId(),
                requestContentType: 'application/json'
        )

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
    }

    def "student can't get another student's failed answers from dashboard"() {
        given:
        def newStudent = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.EXTERNAL)
        newStudent.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        userRepository.save(newStudent)
        externalUserLogin(USER_2_USERNAME, USER_2_PASSWORD)

        when:
        response = restClient.delete(
                path: '/students/failedanswers/' + failedAnswer.getId(),
                requestContentType: 'application/json'
        )

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
    }

    def cleanup() {
        failedAnswerRepository.deleteAll()
        dashboardRepository.deleteAll()
        userRepository.deleteAll()
        courseRepository.deleteAll()
    }


}