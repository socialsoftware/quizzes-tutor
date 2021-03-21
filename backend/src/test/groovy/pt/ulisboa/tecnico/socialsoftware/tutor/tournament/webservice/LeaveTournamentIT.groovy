package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.webservice

class LeaveTournamentIT extends TournamentIT {
    def setup() {
        tournamentDto = createTournamentDto(STRING_DATE_TOMORROW, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)
    }

    def "user leaves tournament"() {
        given: 'user joins tournament'
        tournamentService.joinTournament(user.getId(), tournamentDto.getId(), "")

        when:
        response = restClient.put(
                path: '/tournaments/' + courseExecution.getId() + '/leaveTournament/' + tournamentDto.getId(),
                requestContentType: 'application/json'
        )

        then: "check response status"
        response.status == 200

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
