package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.activity;

import pt.ulisboa.tecnico.socialsoftware.common.activity.QuizActivities;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;

public class QuizActivitiesImpl implements QuizActivities {

    private final QuizService quizService;

    public QuizActivitiesImpl(QuizService quizService) {
        this.quizService = quizService;
    }

    @Override
    public void deleteQuiz(Integer quizId) {
        quizService.removeExternalQuiz(quizId);
    }

    @Override
    public void updateQuiz(Integer userId, Integer executionId, Integer quizId,
            TournamentDto tournamentDto) {
        quizService.updateExternalQuiz(userId, executionId, quizId, tournamentDto);
    }

}
