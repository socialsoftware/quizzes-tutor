package pt.ulisboa.tecnico.socialsoftware.common.remote;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;

public class QuizInterface implements QuizContract {
    @Override
    public QuizDto findQuizById(Integer quizId) {
        return null;
    }

    @Override
    public void updateQuiz(QuizDto quizDto) {

    }

    @Override
    public void deleteExternalQuiz(Integer quizId) {

    }

    @Override
    public Integer generateQuizAndGetId(Integer creatorId, Integer courseExecutionId, ExternalStatementCreationDto quizDetails) {
        return null;
    }
}
