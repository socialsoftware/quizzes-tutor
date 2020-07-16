package pt.ulisboa.tecnico.socialsoftware.tutor.user.webservice

import groovyx.net.http.RESTClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class CreateExternalUserWebServiceIT extends SpockTest {

    @LocalServerPort private int port

    def client

    def loginResponse
    def response

    def setup(){
        client = new RESTClient("http://localhost:" + port)
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)

        loginResponse = client.get(
                path: '/auth/demo/admin'
        )
        client.headers['Authorization']  = "Bearer " + loginResponse.data.token
    }

    def "login as demo admin, and create an external user" () {
        when:
        response = client.post(
                path: '/courses/executions/'+courseExecution.getId()+'/users',
                body: [
                        admin: false,
                        email: 'pedro.pereira2909@gmail.com',
                        role: 'STUDENT'
                ],
                requestContentType: 'application/json'
        )

        then: "check response status"
        response != null
        response.status == 200
        response.data != null
        response.data.username == "pedro.pereira2909@gmail.com"
        response.data.email == "pedro.pereira2909@gmail.com"
        response.data.admin == false
        response.data.role == "STUDENT"

        cleanup:
        courseExecution.getUsers().remove(userRepository.findById(response.data.id).get())
        courseExecutionRepository.deleteUserCourseExecution(courseExecution.getId())
        courseExecution.remove()
        courseExecutionRepository.delete(courseExecution)
        courseRepository.delete(course)
        userRepository.delete(userRepository.findById(response.data.id).get())
    }

}
