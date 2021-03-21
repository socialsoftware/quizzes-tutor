package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.webservice

import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto

class CreateTournamentIT extends TournamentIT {
    def "user creates tournament"() {
        given: 'a tournamentDto'
        def tournamentDto = new TournamentDto()
        tournamentDto.setStartTime(STRING_DATE_TOMORROW)
        tournamentDto.setEndTime(STRING_DATE_LATER)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setCanceled(false)

        when:
        response = restClient.post(
                path: '/tournaments/' + courseExecution.getId(),
                query: ['topicsId': topic1.getId()],
                body: tournamentDto,
                requestContentType: 'application/json'
        )

        then: "check response status"
        response.status == 200
        response.data != null
        and: "if it responds with the correct tournament"
        def tournament = response.data
        tournament.id != null
        DateHandler.toISOString(DateHandler.toLocalDateTime(tournament.startTime)) == STRING_DATE_TOMORROW
        DateHandler.toISOString(DateHandler.toLocalDateTime(tournament.endTime)) == STRING_DATE_LATER
        tournament.topicsDto.id == [topic1.getId()]
        tournament.numberOfQuestions == NUMBER_OF_QUESTIONS
        tournament.canceled == false
        tournament.privateTournament == false
        tournament.password == ''

        cleanup:
        tournamentRepository.delete(tournamentRepository.findById(tournament.id).get())
        courseExecution.getUsers().remove(userRepository.findById(user.getId()).get())
        userRepository.delete(userRepository.findById(user.getId()).get())
    }

    def cleanup() {
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
