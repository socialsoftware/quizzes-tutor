package pt.ulisboa.tecnico.socialsoftware.tournament.workflow.removeTournament;

import com.uber.cadence.workflow.WorkflowMethod;

public interface RemoveTournamentWorkflow {

    /**
     * @return tournamentId
     */
    @WorkflowMethod
    Integer removeTournament(Integer tournamentId, Integer quizId);
}
