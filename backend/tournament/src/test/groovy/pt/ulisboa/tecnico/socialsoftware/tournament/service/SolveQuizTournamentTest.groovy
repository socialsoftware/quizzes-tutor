package pt.ulisboa.tecnico.socialsoftware.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.tournament.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question

@DataJpaTest
class SolveQuizTournamentTest extends TournamentTest {
    def tournamentDto

    def setup() {
        tournamentDto = createTournament(creator1, STRING_DATE_TODAY, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)

        createAssessmentWithTopicConjunction(ASSESSMENT_1_TITLE, Assessment.Status.AVAILABLE, externalCourseExecution)

        createMultipleChoiceQuestion(LOCAL_DATE_TODAY, QUESTION_1_CONTENT, QUESTION_1_TITLE, Question.Status.AVAILABLE, externalCourse)
    }

    def "1 student solve a tournament" () {
        given:
        tournamentRepository.findById(tournamentDto.getId()).orElse(null).addParticipant(participant1, "")

        when:
        def result = tournamentService.solveQuiz(participant1.getId(), tournamentDto.getId())

        then: "solved it"
        result != null
    }

    def "2 student solve a tournament" () {
        given:
        def user2 = createUser(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, Role.STUDENT, externalCourseExecution)
        def participant2 = createTournamentParticipant(user2)
        and:
        tournamentRepository.findById(tournamentDto.getId()).orElse(null).addParticipant(participant1, "")
        tournamentRepository.findById(tournamentDto.getId()).orElse(null).addParticipant(participant2, "")

        when:
        def result1 = tournamentService.solveQuiz(participant1.getId(), tournamentDto.getId())
        def result2 = tournamentService.solveQuiz(participant2.getId(), tournamentDto.getId())

        then: "solved it"
        result1 != null
        result2 != null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}