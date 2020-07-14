package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CancelTournamentTest extends SpockTest {
    def topic1
    def topic2
    def tournamentDto
    def topics = new HashSet<Integer>()
    def user

    def setup() {
        user = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        user.addCourse(courseExecution)
        userRepository.save(user)
        user.setKey(user.getId())

        def topicDto1 = new TopicDto()
        topicDto1.setName(TOPIC_1_NAME)
        topic1 = new Topic(course, topicDto1)
        topicRepository.save(topic1)

        def topicDto2 = new TopicDto()
        topicDto2.setName(TOPIC_2_NAME)
        topic2 = new Topic(course, topicDto2)
        topicRepository.save(topic2)

        topics.add(topic1.getId())
        topics.add(topic2.getId())

        tournamentDto = new TournamentDto()
        tournamentDto.setStartTime(STRING_DATE_TOMORROW)
        tournamentDto.setEndTime(STRING_DATE_LATER)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setState(Tournament.Status.NOT_CANCELED)
    }

    def "user that created tournament cancels it"() {
        given:
        tournamentDto = tournamentService.createTournament(user.getId(), topics, tournamentDto)

        when:
        tournamentService.cancelTournament(user.getId(), tournamentDto)

        then:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getState() == Tournament.Status.CANCELED
    }

    def "user that created an open tournament tries to change it"() {
        given: "a tournament"
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto = tournamentService.createTournament(user.getId(), topics, tournamentDto)

        when:
        tournamentService.cancelTournament(user.getId(), tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_IS_OPEN
        tournamentRepository.count() == 1L
    }

    def "user that created tournament tries to change it after has ended"() {
        given: "a tournament"
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto.setEndTime(STRING_DATE_TODAY)
        tournamentDto = tournamentService.createTournament(user.getId(), topics, tournamentDto)

        when:
        tournamentService.cancelTournament(user.getId(), tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_ALREADY_CLOSED
        tournamentRepository.count() == 1L
    }

    def "user that did not created tournament cancels it"() {
        given:
        tournamentDto = tournamentService.createTournament(user.getId(), topics, tournamentDto)

        and: "a new user"
        def user2 = new User(USER_2_NAME, USER_2_USERNAME, User.Role.STUDENT)
        courseExecution.addUser(user2)
        courseExecutionRepository.save(courseExecution)

        user2.addCourse(courseExecution)
        userRepository.save(user2)

        when:
        tournamentService.cancelTournament(user2.getId(), tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_CREATOR

        and:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getState() == Tournament.Status.NOT_CANCELED
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
