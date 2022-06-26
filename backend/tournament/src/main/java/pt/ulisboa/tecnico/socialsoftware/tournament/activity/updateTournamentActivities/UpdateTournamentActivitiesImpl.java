package pt.ulisboa.tecnico.socialsoftware.tournament.activity.updateTournamentActivities;

import java.util.Set;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;
import pt.ulisboa.tecnico.socialsoftware.tournament.services.local.TournamentProvidedService;

public class UpdateTournamentActivitiesImpl implements UpdateTournamentActivities {

    private final TournamentProvidedService tournamentService;

    public UpdateTournamentActivitiesImpl(TournamentProvidedService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @Override
    public void beginUpdate(Integer tournamentId) {
        tournamentService.beginUpdateTournament(tournamentId);
    }

    @Override
    public void undoUpdate(Integer tournamentId) {
        tournamentService.undoUpdate(tournamentId);
    }

    @Override
    public void updateTopics(Integer tournamentId, Set<TournamentTopic> topics) {
        tournamentService.updateTopics(tournamentId, topics);

    }

    @Override
    public void undoUpdateTopics(Integer tournamentId, Set<TournamentTopic> topics) {
        tournamentService.undoUpdateTopics(tournamentId, topics);
    }

    @Override
    public void confirmUpdate(Integer tournamentId, TournamentDto tournamentDto) {
        tournamentService.confirmUpdate(tournamentId, tournamentDto);
    }

}
