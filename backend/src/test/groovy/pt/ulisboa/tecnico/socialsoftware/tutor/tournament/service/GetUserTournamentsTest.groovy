package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@DataJpaTest
class GetUserTournamentsTest extends TournamentTest {
    def user2

    def setup() {
        user2 = createUser(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, User.Role.STUDENT, externalCourseExecution)
    }

    def "user creates two tournaments"() {
        given: "a tournamentDto1"
        def tournamentDto1 = createTournament(user1, STRING_DATE_TODAY, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)
        and: "a tournamentDto2"
        def tournamentDto2 = createTournament(user1, STRING_DATE_TODAY, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)

        when:
        def result = tournamentService.getTournamentsByUserId(user1.getId())

        then:
        tournamentRepository.count() == 2L
        result.size() == 2
    }

    def "user creates two tournaments and other user creates one tournament"() {
        given: "a tournamentDto1"
        def tournamentDto1 = createTournament(user1, STRING_DATE_TODAY, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)
        and: "a tournamentDto2"
        def tournamentDto2 = createTournament(user1, STRING_DATE_TODAY, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)
        and: "new user creates a tournamentDto3"
        def tournamentDto3 = createTournament(user2, STRING_DATE_TODAY, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)

        when:
        def result = tournamentService.getTournamentsByUserId(user1.getId())

        then:
        tournamentRepository.count() == 3L
        result.size() == 2
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
