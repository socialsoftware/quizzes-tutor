package pt.ulisboa.tecnico.socialsoftware.common.activity;

import com.uber.cadence.activity.ActivityMethod;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.common.utils.Constants;

public interface QuizActivities {

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.TUTOR_TASK_LIST)
    void deleteQuiz(Integer quizId);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.TUTOR_TASK_LIST)
    void updateQuiz(Integer userId, Integer executionId, Integer quizId,
            TournamentDto tournamentDto);

}
