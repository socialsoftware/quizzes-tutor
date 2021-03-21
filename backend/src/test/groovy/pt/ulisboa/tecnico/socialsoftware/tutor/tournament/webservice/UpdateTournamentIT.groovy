package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.webservice

class UpdateTournamentIT extends TournamentIT {
    def setup() {
        tournamentDto = createTournamentDto(STRING_DATE_TOMORROW, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)
    }

    def "user updates tournament"() {
        given: 'a new tournamentDto'
        def newNumberOfQuestions = NUMBER_OF_QUESTIONS + 1
        tournamentDto.setStartTime(STRING_DATE_TOMORROW_PLUS_10_MINUTES)
        tournamentDto.setEndTime(STRING_DATE_LATER_PLUS_10_MINUTES)
        tournamentDto.setNumberOfQuestions(newNumberOfQuestions)

        when:
        response = restClient.put(
                path: '/tournaments/' + courseExecution.getId() + '/updateTournament',
                query: ['topicsId': topic1.getId()],
                body: tournamentDto,
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
