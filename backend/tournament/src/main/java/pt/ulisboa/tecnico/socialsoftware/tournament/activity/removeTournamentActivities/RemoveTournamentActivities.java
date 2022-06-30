package pt.ulisboa.tecnico.socialsoftware.tournament.activity.removeTournamentActivities;

import com.uber.cadence.activity.ActivityMethod;

import pt.ulisboa.tecnico.socialsoftware.common.utils.Constants;

public interface RemoveTournamentActivities {

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.TOURNAMENT_TASK_LIST)
    void beginRemove(Integer tournamentId);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.TOURNAMENT_TASK_LIST)
    void undoRemove(Integer tournamentId);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.TOURNAMENT_TASK_LIST)
    void confirmRemove(Integer tournamentId);

}