package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DifficultQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service.FailedAnswersSpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student

import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetDifficultQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response
    def student
    def dashboard
    def question
    def optionOK
    def optionKO

    def setup() {
        given:
        restClient = new RESTClient("http://localhost:" + port)
        and:
        createExternalCourseAndExecution()
        and:
        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        and:
        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        question.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)
        and:
        optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)
        and:
        optionKO = new Option()
        optionKO.setContent(OPTION_1_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)
        and:
        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)
    }

    def "student gets difficult questions"() {
        given:
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
        and:
        def difficultQuestion = new DifficultQuestion(dashboard, question, 24)
        difficultQuestionRepository.save(difficultQuestion)

        when:
        response = restClient.get(
                path: '/students/dashboards/' + dashboard.getId() + '/difficultquestions/',
                requestContentType: 'application/json'
        )

        then:
        response != null
        response.status == 200
        response.data.id != null
        and:
        difficultQuestionRepository.findAll().size() == 1
        and:
        def result = response.data.get(0)
        result.id == difficultQuestion.getId()
    }

    def "teacher can't get student's difficult questions"() {
        given:
        demoTeacherLogin()

        when:
        response = restClient.get(
                path: '/students/dashboards/' + dashboard.getId() + '/difficultquestions/',
                requestContentType: 'application/json'
        )

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
    }

    def "student can't get another student's difficult questions"() {
        given:
        def newStudent = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.EXTERNAL)
        newStudent.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        newStudent.addCourse(externalCourseExecution)
        userRepository.save(newStudent)
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)
        and:
        def difficultQuestion = new DifficultQuestion(dashboard, question, 24)
        difficultQuestionRepository.save(difficultQuestion)

        when:
        response = restClient.get(
                path: '/students/dashboards/' + dashboard.getId() + '/difficultquestions/',
                requestContentType: 'application/json'
        )

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
    }

    def cleanup() {
        discussionRepository.deleteAll()
        dashboardRepository.deleteAll()
        userRepository.deleteAll()
        courseRepository.deleteAll()
    }
}