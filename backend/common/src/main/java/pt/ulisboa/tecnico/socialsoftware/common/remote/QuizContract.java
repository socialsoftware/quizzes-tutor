package pt.ulisboa.tecnico.socialsoftware.common.remote;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;

public interface QuizContract {
    QuizDto findQuizById(Integer quizId);

    void updateQuiz(QuizDto quizDto);

    void deleteExternalQuiz(Integer quizId);

    Integer generateQuizAndGetId(Integer creatorId, Integer courseExecutionId, ExternalStatementCreationDto quizDetails);
}
