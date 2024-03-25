package pt.ulisboa.tecnico.socialsoftware.tutor.execution.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTestIT
import pt.ulisboa.tecnico.socialsoftware.tutor.demo.DemoUtils
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetCourseExecutionsWebServiceIT extends SpockTestIT {
    @LocalServerPort
    private int port

    def setup() {
        given:
        deleteAll()
        and:
        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        and: 'an external course execution'
        externalCourse = new Course("Software Engineering", Course.Type.EXTERNAL)
        courseRepository.save(externalCourse)
        externalCourseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(externalCourseExecution)
    }

    def "get the course executions that admin user is allowed to see"() {
        given: 'a demon admin'
        demoAdminLogin()

        when: 'the web service is invoked'
        def result = webClient.get()
                .uri('/executions')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(CourseExecutionDto.class)
                .collectList()
                .block()

        then: 'the response contains one course execution'
        result.size() == 1
        result.get(0).name == DemoUtils.COURSE_NAME
        and: 'there are two courses in the database'
        courseRepository.findAll().size() == 2
        courseExecutionRepository.findAll().size() == 2

        cleanup:
        courseRepository.delete(externalCourse)
    }

    def "demo student is not allowed"() {
        given: 'a demon student'
        demoStudentLogin()

        when: 'the web service is invoked'
        webClient.get()
                .uri('/executions')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(CourseExecutionDto.class)
                .collectList()
                .block()

        then: "the request returns 403"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN

        cleanup:
        courseRepository.delete(externalCourse)
    }

}