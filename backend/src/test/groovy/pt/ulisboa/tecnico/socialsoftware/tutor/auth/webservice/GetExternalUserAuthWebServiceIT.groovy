package pt.ulisboa.tecnico.socialsoftware.tutor.auth.webservice


import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTestIT
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto.AuthDto
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto.AuthPasswordDto
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetExternalUserAuthWebServiceIT extends SpockTestIT {
    @LocalServerPort
    private int port

    User user
    Course course
    CourseExecution courseExecution

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)
    }

    def "external user makes a login"() {
        given: "one inactive user with an expired "
        user = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        user.addCourse(courseExecution)
        user.getAuthUser().setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        userRepository.save(user)
        courseExecution.addUser(user)

        when:
        def result = webClient.post()
                .uri('/auth/external')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(new AuthPasswordDto(USER_1_USERNAME, USER_1_PASSWORD))
                .retrieve()
                .bodyToMono(AuthDto.class)
                .block()

        then: "check response status"
        result.token != ""
        result.user.key != null
        result.user.username == USER_1_USERNAME

        cleanup:
        courseExecution.getUsers().remove(userRepository.findByKey(result.user.key).get())
        authUserRepository.delete(userRepository.findByKey(result.user.key).get().getAuthUser())
        userRepository.delete(userRepository.findByKey(result.user.key).get())
    }

    def cleanup() {
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
