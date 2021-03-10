package pt.ulisboa.tecnico.socialsoftware.tutor.api;

import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.answer.dtos.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament.dtos.ExternalStatementCreationDto;

public interface AnswerInterface {

    Integer generateQuizAndGetId(Integer creatorId, Integer courseExecutionId, ExternalStatementCreationDto quizDetails);

    StatementQuizDto startQuiz(Integer userId, Integer quizId);
}
