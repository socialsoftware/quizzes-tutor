package pt.ulisboa.tecnico.socialsoftware.tutor.user.webservice

import groovyx.net.http.RESTClient
import io.swagger.models.auth.In
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UsersIdsDto

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class ConfirmRegistrationWebServiceIT extends SpockTest{
    @LocalServerPort
    private int port

    final static String COURSE_NAME = "Course 1"
    final static String COURSE_ACRONYM = "C1"
    final static String COURSE_ACADEMIC_TERM = "Spring 2020"

    final static String NAME = "User"
    final static String EMAIL = "placeholder@mail.com"
    final static String PASSWORD = "1234"
    final static String TOKEN = "1234abc"
    final static LocalDateTime TOKEN_DATE = LocalDateTime.now()

    def response
    User user

    Course course
    CourseExecution courseExecution
    List<Integer> usersIdsList

    def setup(){
        restClient = new RESTClient("http://localhost:" + port)
        usersIdsList = new ArrayList<>()
        course = new Course(COURSE_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_ACRONYM, COURSE_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)
    }

    def "user confirms registration"() {
        given: "one inactive user with an expired "
        user = new User(NAME, EMAIL, User.Role.STUDENT)
        user.setEmail(EMAIL)
        user.addCourse(courseExecution)
        user.setState(User.State.INACTIVE)
        user.setConfirmationToken(TOKEN)
        user.setTokenGenerationDate(TOKEN_DATE)
        courseExecution.addUser(user)
        userRepository.save(user)

        when:
        response = restClient.post(
                path: '/auth/registration/confirm',
                body: [
                        username: EMAIL,
                        password: PASSWORD,
                        confirmationToken: TOKEN
                ],
                requestContentType: 'application/json'
        )


        then: "check response status"
        response.status == 200
        response.data != null
        response.data.email == EMAIL
        response.data.username == EMAIL
        response.data.state == "ACTIVE"
        response.data.role == "STUDENT"
        
        cleanup:

        courseExecution.getUsers().remove(userRepository.findById((int)response.data.id).get())
        userRepository.delete(userRepository.findById((int)response.data.id).get())
    }

    def "user tries to confirm registration with an expired token"() {
        given: "one inactive user with an expired token"
        user = new User(NAME, EMAIL, User.Role.STUDENT)
        user.setEmail(EMAIL)
        user.addCourse(courseExecution)
        user.setState(User.State.INACTIVE)
        user.setConfirmationToken(TOKEN)
        user.setTokenGenerationDate(LocalDateTime.now().minusDays(2))
        courseExecution.addUser(user)
        userRepository.save(user)

        when:
        response = restClient.post(
                path: '/auth/registration/confirm',
                body: [
                        username: EMAIL,
                        password: PASSWORD,
                        confirmationToken: TOKEN
                ],
                requestContentType: 'application/json'
        )


        then: "check response status"
        response.status == 200
        response.data != null
        response.data.email == EMAIL
        response.data.username == EMAIL
        response.data.state == "INACTIVE"
        response.data.role == "STUDENT"

        cleanup:

        courseExecution.getUsers().remove(userRepository.findById((int)response.data.id).get())
        userRepository.delete(userRepository.findById((int)response.data.id).get())
    }

    def cleanup() {
        courseExecutionRepository.deleteUserCourseExecution(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
