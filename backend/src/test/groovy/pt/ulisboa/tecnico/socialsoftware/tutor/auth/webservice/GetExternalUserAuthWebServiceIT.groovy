package pt.ulisboa.tecnico.socialsoftware.tutor.auth.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetExternalUserAuthWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    User user
    Course course
    CourseExecution courseExecution

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)
    }

    def "external user makes a login"() {
        given: "one inactive user with an expired "
        user = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        user.addCourse(courseExecution)
        user.getAuthUser().setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        userRepository.save(user)
        courseExecution.addUser(user)

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
        response.data.user.key != null
        response.data.user.username == USER_1_EMAIL
        
        cleanup:
        courseExecution.getUsers().remove(userRepository.findByKey(response.data.user.key).get())
        authUserRepository.delete(userRepository.findByKey(response.data.user.key).get().getAuthUser())
        userRepository.delete(userRepository.findByKey(response.data.user.key).get())
    }

    def cleanup() {
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
