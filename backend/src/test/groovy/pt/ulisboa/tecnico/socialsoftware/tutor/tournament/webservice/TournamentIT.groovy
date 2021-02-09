package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TournamentIT extends SpockTest {
    @LocalServerPort
    private int port

    def response
    def user

    def course
    def courseExecution

    def topics
    def topic1
    def topicDto1
    def tournamentDto

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        user = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        user.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        user.addCourse(courseExecution)
        courseExecution.addUser(user)
        userRepository.save(user)

        def loggedUser = restClient.get(
                path: '/auth/external',
                query: [
                        email: USER_1_EMAIL,
                        password: USER_1_PASSWORD,
                ],
                requestContentType: 'application/json'
        )
        restClient.headers['Authorization']  = "Bearer " + loggedUser.data.token

        topicDto1 = new TopicDto()
        topicDto1.setName(TOPIC_1_NAME)
        topic1 = new Topic(course, topicDto1)
        topicRepository.save(topic1)

        topics = new HashSet<Integer>()
        topics.add(topic1.getId())
    }

    def createTournamentDto(String startTime, String endTime, Integer numberOfQuestions, boolean isCanceled) {
        def tournamentDto = new TournamentDto()
        tournamentDto.setStartTime(startTime)
        tournamentDto.setEndTime(endTime)
        tournamentDto.setNumberOfQuestions(numberOfQuestions)
        tournamentDto.setCanceled(isCanceled)
        tournamentDto = tournamentService.createTournament(user.getId(), courseExecution.getId(), topics, tournamentDto)

        return tournamentDto
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
