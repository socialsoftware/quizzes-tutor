package pt.ulisboa.tecnico.socialsoftware.tutor.auth.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExternalUserAuthWebServiceIT extends SpockTest{

    @LocalServerPort
    private int port

    User user

    Course course
    CourseExecution courseExecution

    def setup(){
        restClient = new RESTClient("http://localhost:" + port)
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)
    }

    def "user confirms registration"() {
        given: "one inactive user with an expired "
        user = new User(USER_1_NAME, USER_1_EMAIL, User.Role.STUDENT)
        user.setEmail(USER_1_EMAIL)
        user.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        user.addCourse(courseExecution)
        user.setState(User.State.ACTIVE)
        courseExecution.addUser(user)
        userRepository.save(user)

        when:
        def response = restClient.get(
                path: '/auth/external',
                query: [
                        email: USER_1_EMAIL,
                        password: USER_1_PASSWORD,
                ],
                requestContentType: 'application/json'
        )


        then: "check response status"
        response.status == 200
        response.data.token != ""
        response.data.user.username == USER_1_EMAIL
        
        cleanup:

        courseExecution.getUsers().remove(userRepository.findByUsername(response.data.user.username).get())
        userRepository.delete(userRepository.findByUsername(response.data.user.username).get())
    }

    def cleanup() {
        persistentCourseCleanup()
        courseExecutionRepository.deleteUserCourseExecution(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
