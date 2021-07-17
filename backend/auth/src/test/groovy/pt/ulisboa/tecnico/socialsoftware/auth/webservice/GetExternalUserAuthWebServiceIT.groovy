package pt.ulisboa.tecnico.socialsoftware.auth.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.auth.SpockTest

<<<<<<< HEAD:backend/apigateway/src/test/groovy/pt/ulisboa/tecnico/socialsoftware/apigateway/webservice/auth/GetExternalUserAuthWebServiceIT.groovy


import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthExternalUser

=======

import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUser

>>>>>>> microservices:backend/auth/src/test/groovy/pt/ulisboa/tecnico/socialsoftware/auth/webservice/GetExternalUserAuthWebServiceIT.groovy


import pt.ulisboa.tecnico.socialsoftware.auth.domain.UserSecurityInfo
import pt.ulisboa.tecnico.socialsoftware.common.dtos.course.CourseType
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetExternalUserAuthWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    User user
    AuthUser authUser
    Course course
    CourseExecution courseExecution

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)
        course = new Course(COURSE_1_NAME, CourseType.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, CourseType.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)
    }

    def "external user makes a login"() {
        given: "one inactive user with an expired "
        user = new User(USER_1_NAME, USER_1_EMAIL, Role.STUDENT)
        user.addCourse(courseExecution)
        courseExecution.addUser(user)
        user.setActive(false)
        userRepository.save(user)
        authUser = new AuthExternalUser(new UserSecurityInfo(user.getId(), USER_1_NAME, Role.STUDENT, false), USER_1_EMAIL, USER_1_EMAIL)
        authUser.addCourseExecution(courseExecution.getId())
        authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        authUserRepository.save(authUser)

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
        response.data.user.id != null
        response.data.user.username == USER_1_EMAIL
        
        cleanup:
        courseExecution.getUsers().remove(userRepository.findById(user.getId()).get())
        authUserRepository.delete(authUserRepository.findAuthUserByUsername(response.data.user.username).get())
        userRepository.delete(userRepository.findById(user.getId()).get())
    }

    def cleanup() {
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
