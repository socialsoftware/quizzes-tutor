package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.services.remote;

import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.answer.dtos.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament.dtos.StatementTournamentCreationDto;

public interface AnswerInterface {
    Integer getQuizId(Integer creatorId, Integer courseExecutionId, StatementTournamentCreationDto quizDetails);

    StatementQuizDto startTournamentQuiz(Integer userId, Integer quizId);
}
