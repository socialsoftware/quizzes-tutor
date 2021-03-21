package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.webservice

class JoinTournamentIT extends TournamentIT {
    def setup() {
        tournamentDto = createTournamentDto(STRING_DATE_TOMORROW, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)
    }

    def "user joins tournament"() {
        when:
        response = restClient.put(
                path: '/tournaments/' + courseExecution.getId() + '/joinTournament/' + tournamentDto.getId(),
                query: ['password': ''],
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
