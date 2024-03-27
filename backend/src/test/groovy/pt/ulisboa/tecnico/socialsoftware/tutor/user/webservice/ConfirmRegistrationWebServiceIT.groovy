package pt.ulisboa.tecnico.socialsoftware.tutor.user.webservice


import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTestIT
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfirmRegistrationWebServiceIT extends SpockTestIT {
    @LocalServerPort
    private int port

    def user
    def course
    def courseExecution

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

    def "user confirms registration"() {
        given: "one inactive user"
        user = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        user.addCourse(courseExecution)
        user.getAuthUser().setConfirmationToken(USER_1_TOKEN)
        user.getAuthUser().setTokenGenerationDate(LOCAL_DATE_TODAY)
        courseExecution.addUser(user)
        userRepository.save(user)
        and:
        def externalUserDto = new ExternalUserDto()
        externalUserDto.username = USER_1_USERNAME
        externalUserDto.password = USER_1_PASSWORD
        externalUserDto.confirmationToken = USER_1_TOKEN

        when:
        def result = webClient.post()
                .uri('/users/register/confirm')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(externalUserDto)
                .retrieve()
                .bodyToMono(ExternalUserDto.class)
                .block()

        then:
        result.email == USER_1_EMAIL
        result.username == USER_1_USERNAME
        result.active
        result.role == User.Role.STUDENT

        cleanup:
        courseExecution.getUsers().remove(userRepository.findByKey(result.key).get())
        authUserRepository.delete(userRepository.findByKey(result.key).get().getAuthUser())
        userRepository.delete(userRepository.findByKey(result.key).get())
    }

    def "user tries to confirm registration with an expired token"() {
        given: "one inactive user with an expired token"
        user = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        user.addCourse(courseExecution)
        user.getAuthUser().setConfirmationToken(USER_1_TOKEN)
        user.getAuthUser().setTokenGenerationDate(LOCAL_DATE_BEFORE)
        courseExecution.addUser(user)
        userRepository.save(user)
        and:
        def externalUserDto = new ExternalUserDto()
        externalUserDto.username = USER_1_USERNAME
        externalUserDto.password = USER_1_PASSWORD
        externalUserDto.confirmationToken = USER_1_TOKEN

        when:
        def result = webClient.post()
                .uri('/users/register/confirm')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(externalUserDto)
                .retrieve()
                .bodyToMono(ExternalUserDto.class)
                .block()

        then:
        result.email == USER_1_EMAIL
        result.username == USER_1_USERNAME
        !result.active
        result.role == User.Role.STUDENT

        cleanup:
        courseExecution.getUsers().remove(userRepository.findByKey(result.key).get())
        authUserRepository.delete(userRepository.findByKey(result.key).get().getAuthUser())
        userRepository.delete(userRepository.findByKey(result.key).get())
    }

    def cleanup() {
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
