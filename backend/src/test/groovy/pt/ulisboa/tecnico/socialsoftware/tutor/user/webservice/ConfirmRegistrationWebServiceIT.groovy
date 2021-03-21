package pt.ulisboa.tecnico.socialsoftware.tutor.user.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfirmRegistrationWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response
    def user


    def course
    def courseExecution

    def setup(){
        restClient = new RESTClient("http://localhost:" + port)
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)
    }

    def "user confirms registration"() {
        given: "one inactive user"
        user = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        user.addCourse(courseExecution)
        user.getAuthUser().setConfirmationToken(USER_1_TOKEN)
        user.getAuthUser().setTokenGenerationDate(LOCAL_DATE_TODAY)
        courseExecution.addUser(user)
        userRepository.save(user)

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
        courseExecution.getUsers().remove(userRepository.findByKey(response.data.key).get())
        authUserRepository.delete(userRepository.findByKey(response.data.key).get().getAuthUser())
        userRepository.delete(userRepository.findByKey(response.data.key).get())
    }

    def "user tries to confirm registration with an expired token"() {
        given: "one inactive user with an expired token"
        user = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        user.addCourse(courseExecution)
        user.getAuthUser().setConfirmationToken(USER_1_TOKEN)
        user.getAuthUser().setTokenGenerationDate(LOCAL_DATE_BEFORE)
        courseExecution.addUser(user)
        userRepository.save(user)

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
        courseExecution.getUsers().remove(userRepository.findByKey(response.data.key).get())
        authUserRepository.delete(userRepository.findByKey(response.data.key).get().getAuthUser())
        userRepository.delete(userRepository.findByKey(response.data.key).get())
    }

    def cleanup() {
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
