package pt.ulisboa.tecnico.socialsoftware.tournament.workflow.createTournament;

import com.uber.cadence.workflow.WorkflowMethod;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.common.utils.CadenceConstants;

public interface CreateTournamentWorkflow {

    @WorkflowMethod(executionStartToCloseTimeoutSeconds = 60, taskList = CadenceConstants.TOURNAMENT_TASK_LIST)
    Integer createTournament(Integer tournamentId, Integer creatorId, Integer courseExecutionId,
            ExternalStatementCreationDto externalStatementCreationDto, TopicListDto topicListDto);

}
