package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.webservice

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto

class GetTournamentsIT extends TournamentIT {
    def setup() {
        tournamentDto = createTournament(STRING_DATE_TOMORROW, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)
    }

    def "user gets all tournament"() {
        when:
        def result = webClient.get()
                .uri('/tournaments/' + courseExecution.getId() + '/getTournaments')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(TournamentDto.class)
                .collectList()
                .block()

        then: "check response status"
        result.size() == 1
        result.get(0).numberOfQuestions == NUMBER_OF_QUESTIONS

        cleanup:
        tournamentRepository.delete(tournamentRepository.findById(tournamentDto.getId()).get())
        courseExecution.getUsers().remove(userRepository.findById(user.getId()).get())
        userRepository.delete(userRepository.findById(user.getId()).get())
    }

    def cleanup() {
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
