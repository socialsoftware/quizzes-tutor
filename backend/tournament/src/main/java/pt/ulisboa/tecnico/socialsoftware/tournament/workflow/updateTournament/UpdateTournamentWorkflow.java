package pt.ulisboa.tecnico.socialsoftware.tournament.workflow.updateTournament;

import com.uber.cadence.workflow.WorkflowMethod;

public interface UpdateTournamentWorkflow {

    /**
     * @return tournamentId
     */
    @WorkflowMethod
    Integer updateTournament(Integer tournamentId);

}
