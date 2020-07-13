package pt.ulisboa.tecnico.socialsoftware.tutor.user.webservice

import groovyx.net.http.RESTClient
import org.hibernate.Transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.test.context.ActiveProfiles
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Shared
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@SpringBootTest
@ActiveProfiles("dev")
class CreateExternalUserWebServiceIntegrationTest extends Specification{
    public static final String USER_1_NAME = "User 1 Name"
    public static final String USER_1_USERNAME = "User 1 Username"
    public static final String COURSE_1_NAME = "Course 1 Name"
    public static final String COURSE_1_ACADEMIC_TERM = "1º Semestre 2019/2020"
    public static final String COURSE_1_ACRONYM = "C12"

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    @Shared
    def client = new RESTClient("http://localhost:8080")

    def loginResponse
    def response

    def course
    def courseExecution

    def setup(){

    }


    def "login as demo admin, and create an external user" (){
        given:
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)
        
        loginResponse = client.get(
                path: '/auth/demo/admin'
        )
        client.headers['Authorization']  = "Bearer "+loginResponse.data.token

        when:
        response = client.post(
                path: '/courses/executions/'+courseExecution.getId()+'/users',
                body: [
                        admin: false,
                        email: 'test@mail.com',
                        role: 'STUDENT'
                ],
                requestContentType: 'application/json'
        )

        then: "check response status"
        response != null
        response.status == 200
        response.data != null
        response.data.username == "test@mail.com"
        response.data.email == "test@mail.com"
        response.data.admin == false
        response.data.role == "STUDENT"

        cleanup:
        courseExecution.getUsers().remove(userRepository.findById(response.data.id).get())
        courseExecutionRepository.deleteUserCourseExecution(courseExecution.getId())
        courseExecution.remove()
        courseExecutionRepository.delete(courseExecution)
        courseRepository.delete(course)
        userRepository.delete(userRepository.findById(response.data.id).get())
    }

}
