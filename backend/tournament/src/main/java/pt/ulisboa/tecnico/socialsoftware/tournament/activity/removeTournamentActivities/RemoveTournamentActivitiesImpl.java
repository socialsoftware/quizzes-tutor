package pt.ulisboa.tecnico.socialsoftware.tournament.activity.removeTournamentActivities;

import pt.ulisboa.tecnico.socialsoftware.tournament.services.local.TournamentProvidedService;

public class RemoveTournamentActivitiesImpl implements RemoveTournamentActivities {

    private final TournamentProvidedService tournamentService;

    public RemoveTournamentActivitiesImpl(TournamentProvidedService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @Override
    public void beginRemove(Integer tournamentId) {
        tournamentService.beginRemoveTournament(tournamentId);
    }

    @Override
    public void undoRemove(Integer tournamentId) {
        tournamentService.undoRemoveTournament(tournamentId);

    }

    @Override
    public void confirmRemove(Integer tournamentId) {
        tournamentService.confirmRemoveTournament(tournamentId);
    }

}
