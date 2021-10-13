package pt.ulisboa.tecnico.socialsoftware.tutor.execution.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthExternalUser
import pt.ulisboa.tecnico.socialsoftware.auth.domain.UserSecurityInfo
import pt.ulisboa.tecnico.socialsoftware.common.dtos.course.CourseType

<<<<<<< HEAD:backend/apigateway/src/test/groovy/pt/ulisboa/tecnico/socialsoftware/apigateway/webservice/execution/DeleteExternalInactiveUsersWebServiceIT.groovy


import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role

=======
>>>>>>> microservices:backend/tutor/src/test/groovy/pt/ulisboa/tecnico/socialsoftware/tutor/execution/webservice/DeleteExternalInactiveUsersWebServiceIT.groovy


import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeleteExternalInactiveUsersWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response
    def user1
    def user2
    def authUser1
    def authUser2

    def course1
    def courseExecution1
    def usersIdsList

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)
        usersIdsList = new ArrayList<>()
        course1 = new Course("Demo Course", CourseType.EXTERNAL)
        courseRepository.save(course1)
        courseExecution1 = new CourseExecution(course1, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, CourseType.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution1)
        demoAdminLogin()
    }

    def "there are two inactive external user and deletes them"() {
        given: "two inactive external users"
        user1 = new User(USER_1_NAME, USER_1_USERNAME, Role.STUDENT)
        user1.addCourse(courseExecution1)
        courseExecution1.addUser(user1)
        user1.setActive(false)
        userRepository.save(user1)
        authUser1 = new AuthExternalUser(new UserSecurityInfo(user1.getId(), USER_1_NAME, Role.STUDENT, false), USER_1_USERNAME, USER_1_EMAIL)
        authUser1.addCourseExecution(courseExecution1.getId())
        authUserRepository.save(authUser1)

        user2 = new User(USER_2_NAME, USER_2_USERNAME, Role.TEACHER)
        user2.addCourse(courseExecution1)
        courseExecution1.addUser(user2)
        user2.setActive(false)
        userRepository.save(user2)
        authUser2 = new AuthExternalUser(new UserSecurityInfo(user2.getId(), USER_2_NAME, Role.STUDENT, false), USER_2_USERNAME, USER_2_EMAIL)
        authUser2.addCourseExecution(courseExecution1.getId())
        authUserRepository.save(authUser2)

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
        and: "the users were removed from the database"
        userRepository.findById(user1.getId()).isEmpty()
        userRepository.findById(user2.getId()).isEmpty()

        cleanup:
        courseExecution1.remove()
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution1.getId())
        courseExecutionRepository.deleteById(courseExecution1.getId())
        courseRepository.deleteById(course1.getId())
        authUserRepository.deleteAll()
    }

}