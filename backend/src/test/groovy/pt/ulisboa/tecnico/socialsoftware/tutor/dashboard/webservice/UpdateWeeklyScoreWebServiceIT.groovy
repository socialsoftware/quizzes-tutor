package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTestIT
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.WeeklyScoreDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateWeeklyScoreWebServiceIT extends SpockTestIT {
    @LocalServerPort
    private int port

    def authUserDto
    def courseExecutionDto
    def dashboardDto

    def setup() {
        given:
        deleteAll()
        and:
        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        and:
        courseExecutionDto = courseService.getDemoCourse()
        authUserDto = authUserService.demoStudentAuth(false).getUser()
        dashboardDto = dashboardService.getDashboard(courseExecutionDto.getCourseExecutionId(), authUserDto.getId())
    }

    def "demo student gets its weekly scores"() {
        given:
        demoStudentLogin()

        when:
        def result = webClient.put()
                .uri('/students/dashboards/' + dashboardDto.getId() + '/weeklyscores')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(WeeklyScoreDto.class)
                .collectList()
                .block()

        then:
        result.size() == 1
        def resultWeeklyScore = result.get(0)
        resultWeeklyScore.questionsAnswered == 0
        resultWeeklyScore.questionsUniquelyAnswered == 0
        resultWeeklyScore.percentageCorrect == 0
        resultWeeklyScore.improvedCorrectAnswers == 0
        and:
        weeklyScoreRepository.findAll().size() == 1

        cleanup:
        weeklyScoreRepository.deleteAll()
        dashboardRepository.deleteAll()
    }

    def "demo teacher does not have access"() {
        given:
        demoTeacherLogin()

        when:
        webClient.put()
                .uri('/students/dashboards/' + dashboardDto.getId() + '/weeklyscores')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(WeeklyScoreDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        and:
        weeklyScoreRepository.findAll().size() == 0

        cleanup:
        weeklyScoreRepository.deleteAll()
        dashboardRepository.deleteAll()
    }

    def "student cant update another students failed answers"() {
        given:
        demoStudentLogin(true)

        when:
        webClient.put()
                .uri('/students/dashboards/' + dashboardDto.getId() + '/weeklyscores')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(WeeklyScoreDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        and:
        weeklyScoreRepository.findAll().size() == 0

        cleanup:
        weeklyScoreRepository.deleteAll()
        dashboardRepository.deleteAll()
    }

}