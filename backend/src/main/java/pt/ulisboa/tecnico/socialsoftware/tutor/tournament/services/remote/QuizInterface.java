package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.services.remote;

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;

public interface QuizInterface {

    QuizDto getQuiz(Integer quizId);

    void updateQuiz(QuizDto quizDto);
}
