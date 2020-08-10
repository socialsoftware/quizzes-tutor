package pt.ulisboa.tecnico.socialsoftware.tutor.course.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GetExternalUsersWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port

    Course course1
    Course course2

    CourseExecution courseExecution1
    CourseExecution courseExecution2

    User user1
    User user2

    def response

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course1 = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course1)
        courseExecution1 = new CourseExecution(course1, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution1)

        user1 = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        user1.addCourse(courseExecution1)
        courseExecution1.addUser(user1)
        userRepository.save(user1)

        course2 = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course2)
        courseExecution2 = new CourseExecution(course2, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution2)

        user2 = new User(USER_2_NAME, USER_2_USERNAME, User.Role.STUDENT)
        user2.addCourse(courseExecution2)
        courseExecution2.addUser(user2)
        userRepository.save(user2)


        demoAdminLogin()
    }

    def "get users from courseExecution1"() {
        when:
        response = restClient.get(
            path: '/executions/'+courseExecution1.getId()+'/users/external',
            requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == 200
        and:"if the list contains the correct user"
        def userList = response.data
        userList.size() == 1
        userList.get(0).id == user1.getId()
    }


    def cleanup() {
        persistentCourseCleanup()

        courseExecutionRepository.deleteUserCourseExecution(courseExecution1.getId())
        userRepository.deleteById(user1.getId())
        courseExecutionRepository.deleteById(courseExecution1.getId())
        courseRepository.deleteById(course1.getId())

        courseExecutionRepository.deleteUserCourseExecution(courseExecution2.getId())
        userRepository.deleteById(user2.getId())
        courseExecutionRepository.deleteById(courseExecution2.getId())
        courseRepository.deleteById(course2.getId())
    }
}
