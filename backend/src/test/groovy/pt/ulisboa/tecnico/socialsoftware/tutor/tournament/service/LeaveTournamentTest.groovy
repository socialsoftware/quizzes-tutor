package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class LeaveTournamentTest extends TournamentTest {
    def tournamentDto
    def user2

    def setup() {
        user2 = createUser(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, User.Role.STUDENT, externalCourseExecution)

        tournamentDto = createTournament(user1, STRING_DATE_TODAY, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)

        createMultipleChoiceQuestion(LOCAL_DATE_TODAY, QUESTION_1_CONTENT, QUESTION_1_TITLE, Question.Status.AVAILABLE, externalCourse)
    }

    def "Student leaves the tournament" () {
        given: 'user joins'
        tournamentRepository.findById(tournamentDto.getId()).orElse(null).addParticipant(user1, "")

        when:
        tournamentService.leaveTournament(user1.getId(), tournamentDto.getId())

        then: "the tournament has no participants"
        def result = tournamentRepository.findById(tournamentDto.getId()).orElse(null)
        result.getParticipants().size() == 0
    }

    def "Non participant student leaves the tournament" () {
        when:
        tournamentService.leaveTournament(user2.getId(), tournamentDto.getId())

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == USER_NOT_JOINED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
