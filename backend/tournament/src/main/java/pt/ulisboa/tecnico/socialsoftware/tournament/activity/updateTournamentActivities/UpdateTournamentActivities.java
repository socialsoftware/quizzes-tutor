package pt.ulisboa.tecnico.socialsoftware.tournament.activity.updateTournamentActivities;

import java.util.Set;

import com.uber.cadence.activity.ActivityMethod;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.common.utils.CadenceConstants;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;

public interface UpdateTournamentActivities {

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = CadenceConstants.TOURNAMENT_TASK_LIST)
    void beginUpdate(Integer tournamentId);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = CadenceConstants.TOURNAMENT_TASK_LIST)
    void undoUpdate(Integer tournamentId);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = CadenceConstants.TOURNAMENT_TASK_LIST)
    void updateTopics(Integer tournamentId, Set<TournamentTopic> topics);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = CadenceConstants.TOURNAMENT_TASK_LIST)
    void undoUpdateTopics(Integer tournamentId, Set<TournamentTopic> topics);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = CadenceConstants.TOURNAMENT_TASK_LIST)
    void confirmUpdate(Integer tournamentId, TournamentDto tournamentDto);

}
