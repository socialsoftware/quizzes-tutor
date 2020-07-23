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
@ActiveProfiles("dev")
class ExternalUserAuthWebServiceIT extends SpockTest{

    @LocalServerPort
    private int port

    final static String EMAIL = "placeholder@mail.com"
    final static String PASSWORD = "1234"

    def response
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
        user = new User(USER_1_NAME, EMAIL, User.Role.STUDENT)
        user.setEmail(EMAIL)
        user.setPassword(passwordEncoder.encode(PASSWORD))
        user.addCourse(courseExecution)
        user.setState(User.State.ACTIVE)
        courseExecution.addUser(user)
        userRepository.save(user)

        when:
        response = restClient.get(
                path: '/auth/external',
                query: [
                        email: EMAIL,
                        password: PASSWORD,
                ],
                requestContentType: 'application/json'
        )


        then: "check response status"
        response.status == 200
        response.data.token != ""
        response.data.user.username == EMAIL
        
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
