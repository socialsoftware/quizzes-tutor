package pt.ulisboa.tecnico.socialsoftware.tournament.workflow.removeTournament;

import com.uber.cadence.workflow.WorkflowMethod;

import pt.ulisboa.tecnico.socialsoftware.common.utils.CadenceConstants;

public interface RemoveTournamentWorkflow {

    @WorkflowMethod(executionStartToCloseTimeoutSeconds = 60, taskList = CadenceConstants.TOURNAMENT_TASK_LIST)
    void removeTournament(Integer tournamentId, Integer quizId);
}
