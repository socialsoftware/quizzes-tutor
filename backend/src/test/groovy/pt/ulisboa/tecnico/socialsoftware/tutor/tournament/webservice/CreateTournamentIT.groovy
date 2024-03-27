package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.webservice

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

class CreateTournamentIT extends TournamentIT {
    def "user creates tournament"() {
        given: 'a tournamentDto'
        def tournamentDto = new TournamentDto()
        tournamentDto.setStartTime(STRING_DATE_TOMORROW)
        tournamentDto.setEndTime(STRING_DATE_LATER)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setCanceled(false)

        when:
        def result = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path('/tournaments/' + courseExecution.getId())
                        .queryParam('topicsId', topic1.getId())
                        .build())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(tournamentDto)
                .retrieve()
                .bodyToMono(TournamentDto.class)
                .block()

        then: "if it responds with the correct tournament"
        result.id != null
        DateHandler.toISOString(DateHandler.toLocalDateTime(result.startTime)) == STRING_DATE_TOMORROW
        DateHandler.toISOString(DateHandler.toLocalDateTime(result.endTime)) == STRING_DATE_LATER
        result.topicsDto.id == [topic1.getId()]
        result.numberOfQuestions == NUMBER_OF_QUESTIONS
        !result.canceled
        !result.privateTournament
        result.password == ''

        cleanup:
        tournamentRepository.delete(tournamentRepository.findById(result.id).get())
        courseExecution.getUsers().remove(userRepository.findById(user.getId()).get())
        userRepository.delete(userRepository.findById(user.getId()).get())
    }

    def cleanup() {
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
