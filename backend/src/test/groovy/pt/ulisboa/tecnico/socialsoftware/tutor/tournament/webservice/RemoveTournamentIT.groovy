package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.webservice

class RemoveTournamentIT extends TournamentIT {
    def setup() {
        tournamentDto = createTournament(STRING_DATE_TOMORROW, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)
    }

    def "user removes tournament"() {
        when:
        webClient.delete()
                .uri('/tournaments/' + courseExecution.getId() + '/removeTournament/' + tournamentDto.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(Void.class)
                .block()

        then:
        tournamentRepository.count() == 0

        cleanup:
        courseExecution.getUsers().remove(userRepository.findById(user.getId()).get())
        userRepository.delete(userRepository.findById(user.getId()).get())
    }

    def cleanup() {
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
