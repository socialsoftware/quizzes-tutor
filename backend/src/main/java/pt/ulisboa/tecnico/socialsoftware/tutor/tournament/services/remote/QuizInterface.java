package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.services.remote;

import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.quiz.dtos.QuizDto;

public interface QuizInterface {

    QuizDto getQuiz(Integer quizId);

    void updateQuiz(QuizDto quizDto);

    void deleteQuiz(Integer quizId);
}
