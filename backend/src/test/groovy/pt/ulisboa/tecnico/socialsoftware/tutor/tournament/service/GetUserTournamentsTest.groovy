package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class GetUserTournamentsTest extends SpockTest {
    def topic1
    def topic2
    def topics = new HashSet<Integer>()
    def user1
    def user2

    def setup() {
        user1 = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        user2 = new User(USER_2_NAME, USER_2_USERNAME, User.Role.STUDENT)

        user1.addCourse(courseExecution)
        userRepository.save(user1)
        user1.setKey(user1.getId())

        user2.addCourse(courseExecution)
        userRepository.save(user2)
        user2.setKey(user2.getId())

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
    }

    def "user creates two tournaments"() {
        given: "a tournament"
        def tournamentDto1 = new TournamentDto()
        tournamentDto1.setStartTime(STRING_DATE_TODAY)
        tournamentDto1.setEndTime(STRING_DATE_LATER)
        tournamentDto1.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto1.setState(Tournament.Status.NOT_CANCELED)
        tournamentService.createTournament(user1.getId(), topics, tournamentDto1)

        and: "another tournament"
        def tournamentDto2 = new TournamentDto()
        tournamentDto2.setStartTime(STRING_DATE_TODAY)
        tournamentDto2.setEndTime(STRING_DATE_LATER)
        tournamentDto2.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto2.setState(Tournament.Status.NOT_CANCELED)
        tournamentService.createTournament(user1.getId(), topics, tournamentDto2)

        when:
        def result = tournamentService.getUserTournaments(user1)

        then:
        tournamentRepository.count() == 2L
        result.size() == 2
    }

    def "user creates two tournaments and other user creates one tournament"() {
        given: "a tournament"
        def tournamentDto1 = new TournamentDto()
        tournamentDto1.setStartTime(STRING_DATE_TODAY)
        tournamentDto1.setEndTime(STRING_DATE_LATER)
        tournamentDto1.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto1.setState(Tournament.Status.NOT_CANCELED)
        tournamentService.createTournament(user1.getId(), topics, tournamentDto1)

        and: "another tournament"
        def tournamentDto2 = new TournamentDto()
        tournamentDto2.setStartTime(STRING_DATE_TODAY)
        tournamentDto2.setEndTime(STRING_DATE_LATER)
        tournamentDto2.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto2.setState(Tournament.Status.NOT_CANCELED)
        tournamentService.createTournament(user1.getId(), topics, tournamentDto2)

        and: "new user creates another tournament"
        def tournamentDto3 = new TournamentDto()
        tournamentDto3.setStartTime(STRING_DATE_TODAY)
        tournamentDto3.setEndTime(STRING_DATE_LATER)
        tournamentDto3.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto3.setState(Tournament.Status.NOT_CANCELED)
        tournamentService.createTournament(user2.getId(), topics, tournamentDto3)

        when:
        def result = tournamentService.getUserTournaments(user1)

        then:
        tournamentRepository.count() == 3L
        result.size() == 2
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
