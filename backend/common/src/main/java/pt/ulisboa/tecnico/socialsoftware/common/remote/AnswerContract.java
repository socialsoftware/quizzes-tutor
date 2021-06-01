package pt.ulisboa.tecnico.socialsoftware.common.remote;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;

public interface AnswerContract {

    Integer generateQuizAndGetId(Integer creatorId, Integer courseExecutionId, ExternalStatementCreationDto quizDetails);

    StatementQuizDto startQuiz(Integer userId, Integer quizId);

    StatementQuizDto getStatementQuiz(Integer userId, Integer quizId);
}
