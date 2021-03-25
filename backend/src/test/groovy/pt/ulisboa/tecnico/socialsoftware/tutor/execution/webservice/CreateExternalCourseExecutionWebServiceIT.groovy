package pt.ulisboa.tecnico.socialsoftware.tutor.execution.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DemoUtils

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateExternalCourseExecutionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response

    def setup() {
        given: 'a rest client'
        restClient = new RESTClient("http://localhost:" + port)
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
        response = restClient.post(
                path: '/executions/external',
                body: courseExecutionDto,
                requestContentType: 'application/json'
        )

        then: "the request returns OK"
        response.status == 200
        response.data.courseType == Course.Type.EXTERNAL.toString()
        response.data.courseExecutionType == Course.Type.EXTERNAL.toString()
        response.data.name == DemoUtils.COURSE_NAME
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
        response = restClient.post(
                path: '/executions/external',
                body: courseExecutionDto,
                requestContentType: 'application/json'
        )

        then: "the request returns 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
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
        response = restClient.post(
                path: '/executions/external',
                body: courseExecutionDto,
                requestContentType: 'application/json'
        )

        then: "the request returns 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        and: 'there is a single execution course in the database'
        courseRepository.findAll().size() == 2
        courseExecutionRepository.findAll().size() == 1

        cleanup:
        courseRepository.delete(externalCourse)
    }

}