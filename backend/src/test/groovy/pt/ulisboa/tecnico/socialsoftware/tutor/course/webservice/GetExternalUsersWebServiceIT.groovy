package pt.ulisboa.tecnico.socialsoftware.tutor.course.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetExternalUsersWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution1
    def courseExecution2
    def user1
    def user2

    def response

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course("Demo Course", Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution1 = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution1)

        user1 = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        user1.addCourse(courseExecution1)
        courseExecution1.addUser(user1)
        userRepository.save(user1)

        courseExecution2 = new CourseExecution(course, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution2)

        user2 = new User(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        user2.addCourse(courseExecution2)
        courseExecution2.addUser(user2)
        userRepository.save(user2)

        demoAdminLogin()
    }

    def "get users from courseExecution1"() {
        when:
        response = restClient.get(
            path: '/executions/' + courseExecution1.getId() + '/users/external',
            requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == 200
        and: "if the list contains the correct user"
        def userList = response.data
        userList.size() == 1
        userList.get(0).id == user1.getId()
    }


    def cleanup() {
        persistentCourseCleanup()

        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution1.getId())
        userRepository.deleteById(user1.getId())
        courseExecutionRepository.deleteById(courseExecution1.getId())

        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution2.getId())
        userRepository.deleteById(user2.getId())
        courseExecutionRepository.deleteById(courseExecution2.getId())

        courseRepository.deleteById(course.getId())
    }
}
