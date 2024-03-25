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
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateExternalCourseExecutionWebServiceIT extends SpockTestIT {
    @LocalServerPort
    private int port

    def setup() {
        deleteAll()

        given: 'a rest client'
        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
    }

    def "demo admin creates a demo external course execution"() {
        given: 'a demon admin'
        demoAdminLogin()
        and: 'a course'
        externalCourse = new Course(DemoUtils.COURSE_NAME, Course.Type.EXTERNAL)
        courseRepository.save(externalCourse)
        and: 'a course execution dto'
        def courseExecutionDto = new CourseExecutionDto(externalCourse)
        courseExecutionDto.setCourseType(Course.Type.EXTERNAL)
        courseExecutionDto.setCourseExecutionType(Course.Type.EXTERNAL)
        courseExecutionDto.setName(DemoUtils.COURSE_NAME)
        courseExecutionDto.setAcronym(DemoUtils.COURSE_ACRONYM)
        courseExecutionDto.setAcademicTerm(DemoUtils.COURSE_ACADEMIC_TERM)

        when: 'the web service is invoked'
        def result = webClient.post()
                .uri('/executions/external')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(courseExecutionDto)
                .retrieve()
                .bodyToMono(CourseExecutionDto.class)
                .block()

        then:
        result.courseType == Course.Type.EXTERNAL
        result.courseExecutionType == Course.Type.EXTERNAL
        result.name == DemoUtils.COURSE_NAME
        and: 'there are two courses in the database'
        courseRepository.findAll().size() == 2
        courseExecutionRepository.findAll().size() == 2

        cleanup:
        courseRepository.delete(courseRepository.findById(externalCourse.id).get())
    }

    def "demo student is not allowed"() {
        given: 'a demon student'
        demoStudentLogin()
        and: 'a course'
        externalCourse = new Course(DemoUtils.COURSE_NAME, Course.Type.EXTERNAL)
        courseRepository.save(externalCourse)
        and: 'a course execution dto'
        def courseExecutionDto = new CourseExecutionDto(externalCourse)
        courseExecutionDto.setCourseType(Course.Type.EXTERNAL)
        courseExecutionDto.setCourseExecutionType(Course.Type.EXTERNAL)
        courseExecutionDto.setName(DemoUtils.COURSE_NAME)
        courseExecutionDto.setAcronym(DemoUtils.COURSE_ACRONYM)
        courseExecutionDto.setAcademicTerm(DemoUtils.COURSE_ACADEMIC_TERM)

        when: 'the web service is invoked'
        webClient.post()
                .uri('/executions/external')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(courseExecutionDto)
                .retrieve()
                .bodyToMono(CourseExecutionDto.class)
                .block()

        then: "the request returns 403"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        and: 'there is a single execution course in the database'
        courseRepository.findAll().size() == 2
        courseExecutionRepository.findAll().size() == 1

        cleanup:
        courseRepository.delete(externalCourse)
    }

    def "demo admin can only create demo courses"() {
        given: 'a demon admin'
        demoAdminLogin()
        and: 'a course'
        externalCourse = new Course("Software Engineering", Course.Type.EXTERNAL)
        courseRepository.save(externalCourse)
        and: 'a course execution dto'
        def courseExecutionDto = new CourseExecutionDto(externalCourse)
        courseExecutionDto.setCourseType(Course.Type.EXTERNAL)
        courseExecutionDto.setCourseExecutionType(Course.Type.EXTERNAL)
        courseExecutionDto.setName("Software Engineering")
        courseExecutionDto.setAcronym(DemoUtils.COURSE_ACRONYM)
        courseExecutionDto.setAcademicTerm(DemoUtils.COURSE_ACADEMIC_TERM)

        when: 'the web service is invoked'
        webClient.post()
                .uri('/executions/external')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(courseExecutionDto)
                .retrieve()
                .bodyToMono(CourseExecutionDto.class)
                .block()

        then: "the request returns 403"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        and: 'there is a single execution course in the database'
        courseRepository.findAll().size() == 2
        courseExecutionRepository.findAll().size() == 1

        cleanup:
        courseRepository.delete(externalCourse)
    }

}