package pt.ulisboa.tecnico.socialsoftware.tutor.user.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTestIT
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterExternalUserWebServiceIT extends SpockTestIT {
    @LocalServerPort
    private int port

    def course1
    def courseExecution1

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        course1 = new Course("Demo Course", Course.Type.EXTERNAL)
        courseRepository.save(course1)
        courseExecution1 = new CourseExecution(course1, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution1)
        demoAdminLogin()
    }

    def "login as demo admin, and create an external user"() {
        given:
        def externalUserDto = new ExternalUserDto()
        externalUserDto.username = USER_1_USERNAME
        externalUserDto.email = USER_1_EMAIL
        externalUserDto.admin = false
        externalUserDto.role = 'STUDENT'

        when:
        def result = webClient.post()
                .uri('/users/register/' + courseExecution1.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(externalUserDto)
                .retrieve()
                .bodyToMono(ExternalUserDto.class)
                .block()

        then:
        result.username == USER_1_USERNAME
        result.email == USER_1_EMAIL
        !result.admin
        result.role == User.Role.STUDENT

        cleanup:
        courseExecution1.remove()
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution1.getId())
        courseExecutionRepository.delete(courseExecution1)
        courseRepository.delete(course1)
        authUserRepository.delete(userRepository.findByKey(result.key).get().getAuthUser())
        def user = userRepository.findByKey(result.key).get()
        userRepository.delete(user)
    }

}
