package pt.ulisboa.tecnico.socialsoftware.tutor.user.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterExternalUserWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response

    def course1
    def courseExecution1
    
    def setup(){
        restClient = new RESTClient("http://localhost:" + port)
        course1 = new Course("Demo Course", Course.Type.EXTERNAL)
        courseRepository.save(course1)
        courseExecution1 = new CourseExecution(course1, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution1)
        demoAdminLogin()
    }

    def "login as demo admin, and create an external user" () {
        when:
        response = restClient.post(
                path: '/users/register/' + courseExecution1.getId(),
                body: [
                        admin: false,
                        email: USER_1_EMAIL,
                        role: 'STUDENT'
                ],
                requestContentType: 'application/json'
        )

        then: "check response status"
        response != null
        response.status == 200
        response.data != null
        response.data.username == USER_1_EMAIL
        response.data.email == USER_1_EMAIL
        response.data.admin == false
        response.data.role == "STUDENT"

        cleanup:
        courseExecution1.remove()
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution1.getId())
        courseExecutionRepository.delete(courseExecution1)
        courseRepository.delete(course1)
        authUserRepository.delete(userRepository.findByKey(response.data.key).get().getAuthUser())
        def user = userRepository.findByKey(response.data.key).get()
        userRepository.delete(user)
    }

}
