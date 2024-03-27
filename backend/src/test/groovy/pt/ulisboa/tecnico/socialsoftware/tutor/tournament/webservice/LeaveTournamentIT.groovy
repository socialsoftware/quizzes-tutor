package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.webservice

class LeaveTournamentIT extends TournamentIT {
    def setup() {
        tournamentDto = createTournament(STRING_DATE_TOMORROW, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)
    }

    def "user leaves tournament"() {
        given: 'user joins tournament'
        tournamentService.joinTournament(user.getId(), tournamentDto.getId(), "")

        when:
        webClient.put()
                .uri('/tournaments/' + courseExecution.getId() + '/leaveTournament/' + tournamentDto.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(tournamentDto)
                .retrieve()
                .bodyToMono(Void.class)
                .block()

        then:
        def tournament = tournamentRepository.findById(tournamentDto.getId()).get()
        tournament.participants.size() == 0

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
