package pt.ulisboa.tecnico.socialsoftware.common.remote;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;

public class AnswerInterface implements AnswerContract {
    @Override
    public Integer generateQuizAndGetId(Integer creatorId, Integer courseExecutionId, ExternalStatementCreationDto quizDetails) {
        return null;
    }

    @Override
    public StatementQuizDto startQuiz(Integer userId, Integer quizId) {
        return null;
    }
}
