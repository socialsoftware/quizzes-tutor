package pt.ulisboa.tecnico.socialsoftware.common.activity;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;

public interface QuizActivities {

    void deleteQuiz(Integer quizId);

    void updateQuiz(Integer userId, Integer executionId, Integer quizId,
            TournamentDto tournamentDto);

}
