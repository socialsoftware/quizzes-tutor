package pt.ulisboa.tecnico.socialsoftware.tournament.activity.updateTournamentActivities;

import java.util.Set;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;

public interface UpdateTournamentActivities {

    void beginUpdate(Integer tournamentId);

    void undoUpdate(Integer tournamentId);

    void updateTopics(Integer tournamentId, Set<TournamentTopic> topics);

    void undoUpdateTopics(Integer tournamentId, Set<TournamentTopic> topics);

    void confirmUpdate(Integer tournamentId, TournamentDto tournamentDto);

}
