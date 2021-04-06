package pt.ulisboa.tecnico.socialsoftware.common.remote;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizDto;

public interface QuizInterface {
    QuizDto findQuizById(Integer quizId);

    void updateQuiz(QuizDto quizDto);

    void deleteExternalQuiz(Integer quizId);
}
