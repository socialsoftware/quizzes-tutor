package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class GetAllTournamentsTest extends SpockTest {
    public static final String STRING_DATE_TODAY = DateHandler.toISOString(DateHandler.now())

    def topic1
    def topic2
    def topics = new HashSet<Integer>()
    def user

    def setup() {
        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, false)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)
        user.setKey(user.getId())

        def topicDto1 = new TopicDto()
        topicDto1.setName(TOPIC_1_NAME)
        topic1 = new Topic(externalCourse, topicDto1)
        topicRepository.save(topic1)

        def topicDto2 = new TopicDto()
        topicDto2.setName(TOPIC_2_NAME)
        topic2 = new Topic(externalCourse, topicDto2)
        topicRepository.save(topic2)

        topics.add(topic1.getId())
        topics.add(topic2.getId())
    }

    def "create 1 tournament on time and get all tournaments"() {
        given: 'a tournamentDto'
        def tournamentDto1 = new TournamentDto()
        tournamentDto1.setStartTime(STRING_DATE_TODAY)
        tournamentDto1.setEndTime(STRING_DATE_LATER)
        tournamentDto1.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto1.setState(false)
        tournamentService.createTournament(user.getId(), topics, tournamentDto1)

        when:
        def result = tournamentService.getAllTournaments(user)

        then: "there is no returned data"
        result.size() == 1
    }

    def "create 1 canceled tournament and get all tournaments"() {
        given: 'a tournamentDto'
        def tournamentDto1 = new TournamentDto()
        tournamentDto1.setStartTime(STRING_DATE_TODAY)
        tournamentDto1.setEndTime(STRING_DATE_LATER)
        tournamentDto1.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto1.setState(true)
        tournamentService.createTournament(user.getId(), topics, tournamentDto1)

        when:
        def result = tournamentService.getAllTournaments(user)

        then: "there is no returned data"
        result.size() == 1
    }

    def "create 2 tournaments on time and get all tournaments"() {
        given: 'a tournamentDto'
        def tournamentDto1 = new TournamentDto()
        tournamentDto1.setStartTime(STRING_DATE_TODAY)
        tournamentDto1.setEndTime(STRING_DATE_LATER)
        tournamentDto1.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto1.setState(false)
        tournamentService.createTournament(user.getId(), topics, tournamentDto1)

        and: 'a tournamentDto2'
        def tournamentDto2 = new TournamentDto()
        tournamentDto2.setStartTime(STRING_DATE_TODAY)
        tournamentDto2.setEndTime(STRING_DATE_LATER)
        tournamentDto2.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto2.setState(false)
        tournamentService.createTournament(user.getId(), topics, tournamentDto2)

        when:
        def result = tournamentService.getAllTournaments(user)

        then: "the returned data is correct"
        result.size() == 2
    }


    def "create 2 tournaments out of time and 1 in time and get all tournaments"() {
        given: 'a tournamentDto'
        def tournamentDto1 = new TournamentDto()
        tournamentDto1.setStartTime(STRING_DATE_TOMORROW)
        tournamentDto1.setEndTime(STRING_DATE_LATER)
        tournamentDto1.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto1.setState(false)
        tournamentService.createTournament(user.getId(), topics, tournamentDto1)

        and: 'a tournamentDto2'
        def tournamentDto2 = new TournamentDto()
        tournamentDto2.setStartTime(STRING_DATE_TOMORROW)
        tournamentDto2.setEndTime(STRING_DATE_LATER)
        tournamentDto2.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto2.setState(false)
        tournamentService.createTournament(user.getId(), topics, tournamentDto2)

        and: 'a tournamentDto3'
        def tournamentDto3 = new TournamentDto()
        tournamentDto3.setStartTime(STRING_DATE_TODAY)
        tournamentDto3.setEndTime(STRING_DATE_LATER)
        tournamentDto3.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto3.setState(false)
        tournamentService.createTournament(user.getId(), topics, tournamentDto3)

        when:
        def result = tournamentService.getAllTournaments(user)

        then: "the returned data is correct"
        result.size() == 3
    }

    def "create 2 tournaments out of time and 1 canceled and get all tournaments"() {
        given: 'a tournamentDto'
        def tournamentDto1 = new TournamentDto()
        tournamentDto1.setStartTime(STRING_DATE_TOMORROW)
        tournamentDto1.setEndTime(STRING_DATE_LATER)
        tournamentDto1.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto1.setState(false)
        tournamentService.createTournament(user.getId(), topics, tournamentDto1)

        and: 'a tournamentDto2'
        def tournamentDto2 = new TournamentDto()
        tournamentDto2.setStartTime(STRING_DATE_TOMORROW)
        tournamentDto2.setEndTime(STRING_DATE_LATER)
        tournamentDto2.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto2.setState(false)
        tournamentService.createTournament(user.getId(), topics, tournamentDto2)

        and: 'a tournamentDto3'
        def tournamentDto3 = new TournamentDto()
        tournamentDto3.setStartTime(STRING_DATE_TODAY)
        tournamentDto3.setEndTime(STRING_DATE_LATER)
        tournamentDto3.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto3.setState(true)
        tournamentService.createTournament(user.getId(), topics, tournamentDto3)

        when:
        def result = tournamentService.getAllTournaments(user)

        then: "the returned data is correct"
        result.size() == 3
    }

    def "create 0 tournaments and get all tournaments"() {
        given: 'nothing'

        when:
        def result = tournamentService.getAllTournaments(user)

        then: "there is no returned data"
        result.size() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}