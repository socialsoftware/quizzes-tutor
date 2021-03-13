package pt.ulisboa.tecnico.socialsoftware.tutor.api;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dtos.tournament.ExternalStatementCreationDto;

public interface AnswerInterface {

    Integer generateQuizAndGetId(Integer creatorId, Integer courseExecutionId, ExternalStatementCreationDto quizDetails);

    StatementQuizDto startQuiz(Integer userId, Integer quizId);
}
