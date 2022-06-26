package pt.ulisboa.tecnico.socialsoftware.tournament.workflow.updateTournament;

import java.util.Set;

import com.uber.cadence.workflow.WorkflowMethod;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;

public interface UpdateTournamentWorkflow {

    @WorkflowMethod
    void updateTournament(Integer tournamentId, TournamentDto newTournamentDto, TournamentDto oldTournamentDto,
            TopicListDto topicListDto, Set<TournamentTopic> oldTopics, Integer executionId);

}
