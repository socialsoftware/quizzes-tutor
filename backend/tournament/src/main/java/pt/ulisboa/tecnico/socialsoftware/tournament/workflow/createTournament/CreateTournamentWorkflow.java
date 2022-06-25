package pt.ulisboa.tecnico.socialsoftware.tournament.workflow.createTournament;

import com.uber.cadence.workflow.WorkflowMethod;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;

public interface CreateTournamentWorkflow {

    /**
     * @return tournamentId
     */
    @WorkflowMethod
    Integer createTournament(Integer tournamentId, Integer creatorId, Integer courseExecutionId,
            ExternalStatementCreationDto externalStatementCreationDto, TopicListDto topicListDto);

}
