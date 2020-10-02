package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class JoinTournamentTest extends TournamentTest {
    def tournamentDto
    def privateTournamentDto

    def setup() {
        tournamentDto = createTournament(user1, STRING_DATE_TODAY, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)

        privateTournamentDto = createPrivateTournament(user1, STRING_DATE_TODAY, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false, '123')

        createMultipleChoiceQuestion(LOCAL_DATE_TODAY, QUESTION_1_CONTENT, QUESTION_1_TITLE, Question.Status.AVAILABLE, externalCourse)
    }

    def "1 student join an open tournament and get participants" () {
        when:
        tournamentService.joinTournament(user1.getId(), tournamentDto.getId(), "")

        then: "the students have joined the tournament"
        def result = tournamentRepository.findById(tournamentDto.getId()).orElse(null)
        result.getParticipants().size() == 1
        def resTournamentParticipant1 = result.getParticipants().first()
        resTournamentParticipant1.getName() == USER_1_NAME
        resTournamentParticipant1.getUsername() == USER_1_USERNAME
    }

    def "Student tries to join canceled tournament" () {
        given: 'a canceled tournament'
        def canceledTournamentDto = createTournament(user1, STRING_DATE_TODAY, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, true)

        when:
        tournamentService.joinTournament(user1.getId(), canceledTournamentDto.getId(), "")

        then: "student cannot join"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_CANCELED
        and: "the tournament has no participants"
        def result = tournamentRepository.findById(canceledTournamentDto.getId()).orElse(null)
        result.getParticipants().size() == 0
    }

    def "Student tries to join not open tournament" () {
        given: 'a not open tournament'
        def notOpenTournamentDto = createTournament(user1, STRING_DATE_TODAY, STRING_DATE_TODAY, NUMBER_OF_QUESTIONS, false)

        when:
        tournamentService.joinTournament(user1.getId(), notOpenTournamentDto.getId(), "")

        then: "student cannot join"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_NOT_OPEN
        and: "the tournament has no participants"
        def result = tournamentRepository.findById(notOpenTournamentDto.getId()).orElse(null)
        result.getParticipants().size() == 0
    }

    def "Student tries to join tournament twice" () {
        given:
        tournamentRepository.findById(tournamentDto.getId()).orElse(null).addParticipant(user1, "")

        when:
        tournamentService.joinTournament(user1.getId(), tournamentDto.getId(), "")

        then: "student cannot join"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == DUPLICATE_TOURNAMENT_PARTICIPANT
        and: "the tournament has 1 participant"
        def result = tournamentRepository.findById(tournamentDto.getId()).orElse(null)
        result.getParticipants().size() == 1
        def resTournamentParticipant1 = result.getParticipants().first()
        resTournamentParticipant1.getName() == USER_1_NAME
        resTournamentParticipant1.getUsername() == USER_1_USERNAME
    }

    def "Non-existing student tries to join tournament" () {
        given: 'a non-existing user'
        def fakeUserId = 99

        when:
        tournamentService.joinTournament(fakeUserId, tournamentDto.getId(), "")

        then: "student cannot join"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == USER_NOT_FOUND
        and: "the tournament has no participants"
        def result = tournamentRepository.findById(tournamentDto.getId()).orElse(null)
        result.getParticipants().size() == 0
    }

    def "student joins an open and private tournament with correct password" () {
        when:
        tournamentService.joinTournament(user1.getId(), privateTournamentDto.getId(), "123")

        then: "the student have joined the tournament"
        def result = tournamentRepository.findById(privateTournamentDto.getId()).orElse(null)
        result.getParticipants().size() == 1
        def resTournamentParticipant1 = result.getParticipants().first()
        resTournamentParticipant1.getName() == USER_1_NAME
        resTournamentParticipant1.getUsername() == USER_1_USERNAME
    }

    def "student joins an open and private tournament with wrong password" () {
        when:
        tournamentService.joinTournament(user1.getId(), privateTournamentDto.getId(), "Not 123")

        then: "receives exception"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == WRONG_TOURNAMENT_PASSWORD
        and: "tournament has no participants"
        def result = tournamentRepository.findById(privateTournamentDto.getId()).orElse(null)
        result.getParticipants().size() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
