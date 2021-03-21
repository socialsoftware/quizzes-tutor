package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.webservice

class RemoveTournamentIT extends TournamentIT {
    def setup() {
        tournamentDto = createTournamentDto(STRING_DATE_TOMORROW, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)
    }

    def "user removes tournament"() {
        when:
        response = restClient.delete(
                path: '/tournaments/' + courseExecution.getId() + '/removeTournament/' + tournamentDto.getId(),
                requestContentType: 'application/json'
        )

        then: "check response status"
        response.status == 200

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
