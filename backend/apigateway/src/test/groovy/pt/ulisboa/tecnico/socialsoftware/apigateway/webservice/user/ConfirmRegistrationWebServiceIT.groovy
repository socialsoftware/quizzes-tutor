package pt.ulisboa.tecnico.socialsoftware.apigateway.webservice.user

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.apigateway.SpockTestIT
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthExternalUser
import pt.ulisboa.tecnico.socialsoftware.auth.domain.UserSecurityInfo
import pt.ulisboa.tecnico.socialsoftware.common.dtos.course.CourseType
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfirmRegistrationWebServiceIT extends SpockTestIT {
    @LocalServerPort
    private int port

    def response
    def user
    def authUser

    def course
    def courseExecution

    def setup(){
        restClient = new RESTClient("http://localhost:" + port)
        course = new Course(COURSE_1_NAME, CourseType.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, CourseType.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)
    }

    def "user confirms registration"() {
        given: "one inactive user"
        user = new User(USER_1_NAME, USER_1_EMAIL, Role.STUDENT, false)
        user.addCourse(courseExecution)
        courseExecution.addUser(user)
        userRepository.save(user)
        authUser = new AuthExternalUser(new UserSecurityInfo(user.getId(), USER_1_NAME, Role.STUDENT, false), USER_1_EMAIL, USER_1_EMAIL)
        authUser.addCourseExecution(courseExecution.getId())
        authUser.setConfirmationToken(USER_1_TOKEN)
        authUser.setTokenGenerationDate(LOCAL_DATE_TODAY)
        authUserRepository.save(authUser)

        when:
        response = restClient.post(
                path: '/users/register/confirm',
                body: [
                        username: USER_1_EMAIL,
                        password: USER_1_PASSWORD,
                        confirmationToken: USER_1_TOKEN
                ], 
                requestContentType: 'application/json'
        )


        then: "check response status"
        response.status == 200
        response.data != null
        response.data.email == USER_1_EMAIL
        response.data.username == USER_1_EMAIL
        response.data.active == true
        response.data.role == "STUDENT"
        
        cleanup:
        courseExecution.getUsers().remove(userRepository.findById(response.data.id).get())
        authUserRepository.delete(authUserRepository.findAuthUserByUsername(response.data.username).get())
        userRepository.delete(userRepository.findById(response.data.id).get())
    }

    def "user tries to confirm registration with an expired token"() {
        given: "one inactive user with an expired token"
        user = new User(USER_1_NAME, USER_1_EMAIL, Role.STUDENT, false)
        user.addCourse(courseExecution)
        courseExecution.addUser(user)
        userRepository.save(user)
        authUser = new AuthExternalUser(new UserSecurityInfo(user.getId(), USER_1_NAME, Role.STUDENT, false), USER_1_EMAIL, USER_1_EMAIL)
        authUser.addCourseExecution(courseExecution.getId())
        authUser.setConfirmationToken(USER_1_TOKEN)
        authUser.setTokenGenerationDate(LOCAL_DATE_BEFORE)
        authUserRepository.save(authUser)

        when:
        response = restClient.post(
                path: '/users/register/confirm',
                body: [
                        username: USER_1_EMAIL,
                        password: USER_1_PASSWORD,
                        confirmationToken: USER_1_TOKEN
                ],
                requestContentType: 'application/json'
        )


        then: "check response status"
        response.status == 200
        response.data != null
        response.data.email == USER_1_EMAIL
        response.data.username == USER_1_EMAIL
        response.data.active == false
        response.data.role == "STUDENT"

        cleanup:
        courseExecution.getUsers().remove(userRepository.findById(response.data.id).get())
        authUserRepository.delete(authUserRepository.findAuthUserByUsername(response.data.username).get())
        userRepository.delete(userRepository.findById(response.data.id).get())
    }

    def cleanup() {
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
