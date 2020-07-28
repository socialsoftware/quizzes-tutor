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
@ActiveProfiles("dev")
class DeleteExternalInactiveUsersWebServiceIT extends SpockTest{
    @LocalServerPort
    private int port

    def response
    User user1
    User user2

    Course course1
    CourseExecution courseExecution1
    List<Integer> usersIdsList

    def setup(){
        restClient = new RESTClient("http://localhost:" + port)
        usersIdsList = new ArrayList<>()
        course1 = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course1)
        courseExecution1 = new CourseExecution(course1, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution1)
        demoAdminLogin()
    }

    def "there are two inactive external user and deletes them"() {
        given: "two inactive external users"
        user1 = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        user1.addCourse(courseExecution1)
        user1.setState(User.State.INACTIVE)
        courseExecution1.addUser(user1)
        userRepository.save(user1)

        user2 = new User(USER_2_NAME, USER_2_USERNAME, User.Role.STUDENT)
        user2.addCourse(courseExecution1)
        user2.setState(User.State.INACTIVE)
        courseExecution1.addUser(user2)
        userRepository.save(user2)

        and:"a user ids list"
        usersIdsList.add(user1.getId())
        usersIdsList.add(user2.getId())

        when:
        response = restClient.post(
                path: '/executions/'+courseExecution1.getId()+'/users/delete/',
                body:
                        usersIdsList,
                requestContentType: 'application/json'
        )


        then: "check response status"
        response.status == 200

        cleanup:

        courseExecutionRepository.deleteUserCourseExecution(courseExecution1.getId())
        courseExecutionRepository.deleteById(courseExecution1.getId())
        courseRepository.deleteById(course1.getId())
    }

    def cleanup() {
        persistentCourseCleanup()
    }
}
