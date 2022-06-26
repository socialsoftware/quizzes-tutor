package pt.ulisboa.tecnico.socialsoftware.tournament.workflow.createTournament;

import com.uber.cadence.workflow.WorkflowMethod;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.common.utils.Constants;

public interface CreateTournamentWorkflow {

    @WorkflowMethod(executionStartToCloseTimeoutSeconds = 600, taskList = Constants.TOURNAMENT_TASK_LIST)
    Integer createTournament(Integer tournamentId, Integer creatorId, Integer courseExecutionId,
            ExternalStatementCreationDto externalStatementCreationDto, TopicListDto topicListDto);

}
