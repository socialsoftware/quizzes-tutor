package pt.ulisboa.tecnico.socialsoftware.tutor.api;

import pt.ulisboa.tecnico.socialsoftware.dtos.quiz.QuizDto;

public interface QuizInterface {
    QuizDto findQuizById(Integer quizId);

    void updateQuiz(QuizDto quizDto);

    void deleteExternalQuiz(Integer quizId);
}
