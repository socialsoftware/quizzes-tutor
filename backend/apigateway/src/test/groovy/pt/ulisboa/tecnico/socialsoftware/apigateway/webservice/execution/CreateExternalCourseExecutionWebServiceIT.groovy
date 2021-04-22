package pt.ulisboa.tecnico.socialsoftware.apigateway.webservice.execution

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.apigateway.SpockTest
import pt.ulisboa.tecnico.socialsoftware.apigateway.SpockTestIT
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.common.dtos.course.CourseType
import pt.ulisboa.tecnico.socialsoftware.tutor.demoutils.TutorDemoUtils

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateExternalCourseExecutionWebServiceIT extends SpockTestIT {
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
        externalCourse = new Course(TutorDemoUtils.COURSE_NAME, CourseType.EXTERNAL)
        courseRepository.save(externalCourse)
        and: 'a course execution dto'
        def courseExecutionDto = externalCourse.getCourseExecutionDto()
        courseExecutionDto.setCourseType(CourseType.EXTERNAL)
        courseExecutionDto.setCourseExecutionType(CourseType.EXTERNAL)
        courseExecutionDto.setName(TutorDemoUtils.COURSE_NAME)
        courseExecutionDto.setAcronym(TutorDemoUtils.COURSE_ACRONYM)
        courseExecutionDto.setAcademicTerm(TutorDemoUtils.COURSE_ACADEMIC_TERM)

        when: 'the web service is invoked'
        response = restClient.post(
                path: '/executions/external',
                body: courseExecutionDto,
                requestContentType: 'application/json'
        )

        then: "the request returns OK"
        response.status == 200
        response.data.courseType == CourseType.EXTERNAL.toString()
        response.data.courseExecutionType == CourseType.EXTERNAL.toString()
        response.data.name == TutorDemoUtils.COURSE_NAME
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
        externalCourse = new Course(TutorDemoUtils.COURSE_NAME, CourseType.EXTERNAL)
        courseRepository.save(externalCourse)
        and: 'a course execution dto'
        def courseExecutionDto = externalCourse.getCourseExecutionDto()
        courseExecutionDto.setCourseType(CourseType.EXTERNAL)
        courseExecutionDto.setCourseExecutionType(CourseType.EXTERNAL)
        courseExecutionDto.setName(TutorDemoUtils.COURSE_NAME)
        courseExecutionDto.setAcronym(TutorDemoUtils.COURSE_ACRONYM)
        courseExecutionDto.setAcademicTerm(TutorDemoUtils.COURSE_ACADEMIC_TERM)

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
        externalCourse = new Course("Software Engineering", CourseType.EXTERNAL)
        courseRepository.save(externalCourse)
        and: 'a course execution dto'
        def courseExecutionDto = externalCourse.getCourseExecutionDto()
        courseExecutionDto.setCourseType(CourseType.EXTERNAL)
        courseExecutionDto.setCourseExecutionType(CourseType.EXTERNAL)
        courseExecutionDto.setName("Software Engineering")
        courseExecutionDto.setAcronym(TutorDemoUtils.COURSE_ACRONYM)
        courseExecutionDto.setAcademicTerm(TutorDemoUtils.COURSE_ACADEMIC_TERM)

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