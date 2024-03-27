package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.webservice

class JoinTournamentIT extends TournamentIT {
    def setup() {
        tournamentDto = createTournament(STRING_DATE_TOMORROW, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)
    }

    def "user joins tournament"() {
        when:
        webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path('/tournaments/' + courseExecution.getId() + '/joinTournament/' + tournamentDto.getId())
                        .queryParam('password', '')
                        .build())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(tournamentDto)
                .retrieve()
                .bodyToMono(Void.class)
                .block()

        then: "check response status"
        def tournament = tournamentRepository.findById(tournamentDto.getId()).get()
        tournament.participants.size() == 1

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
