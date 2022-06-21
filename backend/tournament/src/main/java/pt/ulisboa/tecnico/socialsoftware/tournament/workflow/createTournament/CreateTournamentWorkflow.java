package pt.ulisboa.tecnico.socialsoftware.tournament.workflow.createTournament;

import com.uber.cadence.workflow.WorkflowMethod;

public interface CreateTournamentWorkflow {

    /**
     * @return tournamentId
     */
    @WorkflowMethod
    Integer createTournament(Integer tournamentId, Integer quizId);

}
