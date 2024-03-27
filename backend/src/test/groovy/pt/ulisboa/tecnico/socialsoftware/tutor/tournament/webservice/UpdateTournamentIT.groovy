package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.webservice


import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

import java.time.temporal.ChronoUnit

class UpdateTournamentIT extends TournamentIT {
    def setup() {
        tournamentDto = createTournament(STRING_DATE_TOMORROW, STRING_DATE_LATER, NUMBER_OF_QUESTIONS, false)
    }

    def "user updates tournament"() {
        given: 'a new tournamentDto'
        def newNumberOfQuestions = NUMBER_OF_QUESTIONS + 1
        tournamentDto.setStartTime(STRING_DATE_TOMORROW_PLUS_10_MINUTES)
        tournamentDto.setEndTime(STRING_DATE_LATER_PLUS_10_MINUTES)
        tournamentDto.setNumberOfQuestions(newNumberOfQuestions)

        when:
        webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path('/tournaments/' + courseExecution.getId() + '/updateTournament')
                        .queryParam('topicsId', topic1.getId())
                        .build())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(tournamentDto)
                .retrieve()
                .bodyToMono(Void.class)
                .block()

        then: "check response status"
        tournamentRepository.count() == 1
        def tournament = tournamentRepository.findById(tournamentDto.getId()).get()
        tournament.id == tournamentDto.id
        tournament.numberOfQuestions == newNumberOfQuestions
        tournament.startTime.truncatedTo(ChronoUnit.SECONDS) == DateHandler.toLocalDateTime(STRING_DATE_TOMORROW_PLUS_10_MINUTES).truncatedTo(ChronoUnit.SECONDS)
        tournament.endTime.truncatedTo(ChronoUnit.SECONDS) == DateHandler.toLocalDateTime(STRING_DATE_LATER_PLUS_10_MINUTES).truncatedTo(ChronoUnit.SECONDS)

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
