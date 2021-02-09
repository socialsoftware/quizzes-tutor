package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class RemoveTournamentTest extends TournamentTest {
    def tournamentDto

    def setup() {
        tournamentDto = new TournamentDto()
        tournamentDto.setEndTime(STRING_DATE_LATER)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setCanceled(false)

        createAssessmentWithTopicConjunction(ASSESSMENT_1_TITLE, Assessment.Status.AVAILABLE, externalCourseExecution)

        def question1 = createMultipleChoiceQuestion(LOCAL_DATE_TODAY, QUESTION_1_CONTENT, QUESTION_1_TITLE, Question.Status.AVAILABLE, externalCourse)

        createOption(OPTION_1_CONTENT, question1)
    }

    def "user that created tournament removes it"() {
        given:
        tournamentDto.setStartTime(STRING_DATE_TOMORROW)
        tournamentDto = tournamentService.createTournament(user1.getId(), externalCourseExecution.getId(), topics, tournamentDto)

        when:
        tournamentService.removeTournament(tournamentDto.getId())

        then:
        tournamentRepository.count() == 0L
    }

    def "user that created an open tournament tries to remove it"() {
        given: "a tournament"
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto = tournamentService.createTournament(user1.getId(), externalCourseExecution.getId(), topics, tournamentDto)

        when:
        tournamentService.removeTournament(tournamentDto.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_IS_OPEN
        tournamentRepository.count() == 1L
    }

    def "user that created tournament tries to remove it after has ended with no answers"() {
        given: "a tournament"
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto.setEndTime(STRING_DATE_TODAY)
        tournamentDto = tournamentService.createTournament(user1.getId(), externalCourseExecution.getId(), topics, tournamentDto)

        when:
        tournamentService.removeTournament(tournamentDto.getId())

        then:
        tournamentRepository.count() == 0L
    }

    def "user that created tournament tries to remove it with answers"() {
        given: "a tournament"
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto = tournamentService.createTournament(user1.getId(), externalCourseExecution.getId(), topics, tournamentDto)
        and: "join a tournament"
        tournamentRepository.findById(tournamentDto.getId()).orElse(null).addParticipant(user1, "")
        and: "solve a tournament"
        tournamentService.solveQuiz(user1.getId(), tournamentDto.getId())
        and: "is now closed"
        tournamentDto.setEndTime(STRING_DATE_TODAY)

        when:
        tournamentService.removeTournament(tournamentDto.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_IS_OPEN
        tournamentRepository.count() == 1L
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
