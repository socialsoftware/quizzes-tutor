package pt.ulisboa.tecnico.socialsoftware.tutor.api;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto;

public interface AnswerContract {

    Integer generateQuizAndGetId(Integer creatorId, Integer courseExecutionId, ExternalStatementCreationDto quizDetails);

    StatementQuizDto startQuiz(Integer userId, Integer quizId);
}
