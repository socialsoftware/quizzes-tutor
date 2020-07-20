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

    @LocalServerPort
    private int port

    def response

    Course course1
    CourseExecution courseExecution1

    final static String EMAIL = "pedro.pereira2909@gmail.com"

    @Override
    def setup(){
        restClient = new RESTClient("http://localhost:" + port)
        course1 = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course1)
        courseExecution1 = new CourseExecution(course1, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution1)
        demoAdminLogin()
    }

    def "login as demo admin, and create an external user" () {
        when:
        response = restClient.post(
                path: '/courses/executions/'+courseExecution1.getId()+'/users',
                body: [
                        admin: false,
                        email: EMAIL,
                        role: 'STUDENT'
                ],
                requestContentType: 'application/json'
        )

        then: "check response status"
        response != null
        response.status == 200
        response.data != null
        response.data.username == EMAIL
        response.data.email == EMAIL
        response.data.admin == false
        response.data.role == "STUDENT"

        cleanup:
        courseExecution1.getUsers().remove(userRepository.findById(response.data.id).get())
        courseExecutionRepository.deleteUserCourseExecution(courseExecution1.getId())
        courseExecution1.remove()
        courseExecutionRepository.delete(courseExecution1)
        courseRepository.delete(course1)
        userRepository.delete(userRepository.findById(response.data.id).get())

    }

    def cleanup() {

    }

}
