package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTestIT
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto.AuthDto
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto.AuthPasswordDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicSourceDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicTreeDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

/** Question generation is for teachers with their own account, not for the public demo users. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GenerationAccessWebServiceIT extends SpockTestIT {
    @LocalServerPort
    private int port

    def demoExecutionId

    def setup() {
        deleteAll()
        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        demoExecutionId = courseService.getDemoCourse().getCourseExecutionId()
    }

    def get(String uri) {
        return webClient.get().uri(uri)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve().bodyToMono(String.class).block()
    }

    def "the demo teacher is told generation is not in the demo, and is not offered it"() {
        when:
        def auth = webClient.get().uri('/auth/demo/teacher').retrieve().bodyToMono(AuthDto.class).block()
        headers.setBearerAuth(auth.token)

        then:
        !auth.user.questionGeneration

        when:
        get('/generation/' + demoExecutionId + '/jobs')

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        error.responseBodyAsString.contains('not in the demo')
    }

    def "the demo teacher cannot ask for a suggested reply"() {
        given:
        demoTeacherLogin()

        when:
        webClient.post().uri('/discussions/1/replies/suggest')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve().bodyToMono(String.class).block()

        then:
        def error = thrown(WebClientResponseException)
        error.responseBodyAsString.contains('not in the demo')
    }

    def "the demo teacher cannot place pieces of documents under a topic"() {
        given:
        demoTeacherLogin()

        when:
        webClient.put().uri('/topics/1/sources')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue([])
                .retrieve().bodyToMono(String.class).block()

        then:
        def error = thrown(WebClientResponseException)
        error.responseBodyAsString.contains('not in the demo')
    }

    def "the demo teacher cannot put pieces of documents under topics by saving a topic tree"() {
        given:
        demoTeacherLogin()
        def demoCourseId = courseService.getDemoCourse().getCourseId()
        def node = new TopicTreeDto.Node('a', 'Networks', null)
        node.setSources([new TopicSourceDto('m1', 'm1:0')])

        when:
        webClient.put().uri('/topics/courses/' + demoCourseId + '/tree')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(new TopicTreeDto([node]))
                .retrieve().bodyToMono(String.class).block()

        then:
        def error = thrown(WebClientResponseException)
        error.responseBodyAsString.contains('not in the demo')
    }

    def "the demo teacher can still save a topic tree without pieces of documents"() {
        given:
        demoTeacherLogin()
        def demoCourseId = courseService.getDemoCourse().getCourseId()

        when:
        def saved = webClient.put().uri('/topics/courses/' + demoCourseId + '/tree')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(new TopicTreeDto([new TopicTreeDto.Node('a', 'Networks ' + System.nanoTime(), null)]))
                .retrieve().bodyToMono(String.class).block()

        then:
        saved.contains('Networks')
    }

    def "a teacher who logged in with an account is offered generation and can use it"() {
        given:
        createExternalCourseAndExecution()
        def teacher = new Teacher(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.authUser.setActive(true)
        teacher.addCourse(externalCourseExecution)
        externalCourseExecution.addUser(teacher)
        userRepository.save(teacher)

        when:
        def auth = webClient.post().uri('/auth/external')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(new AuthPasswordDto(USER_1_USERNAME, USER_1_PASSWORD))
                .retrieve().bodyToMono(AuthDto.class).block()
        headers.setBearerAuth(auth.token)

        then:
        auth.user.questionGeneration
        get('/generation/' + externalCourseExecution.getId() + '/jobs') == '[]'
    }

    def cleanup() {
        deleteAll()
        // deleteAll leaves users and courses: the next tests create the same usernames
        userRepository.deleteAll()
        courseRepository.deleteAll()
    }
}
