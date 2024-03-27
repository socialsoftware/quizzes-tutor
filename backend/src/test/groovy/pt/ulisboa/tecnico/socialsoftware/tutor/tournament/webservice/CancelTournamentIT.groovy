package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.webservice

class CancelTournamentIT extends TournamentIT {
    def setup() {
        tournamentDto = createTournament(STRING_DATE_TOMORROW, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)
    }

    def "user cancels tournament"() {
        when:
        webClient.put()
                .uri('/tournaments/' + courseExecution.getId() + '/cancelTournament/' + tournamentDto.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(tournamentDto)
                .retrieve()
                .bodyToMono(Void.class)
                .block()

        then:
        def tournament = tournamentRepository.findById(tournamentDto.getId()).get()
        tournament.isCanceled()

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
