package pt.ulisboa.tecnico.socialsoftware.tournament.activity.updateTournamentActivities;

import pt.ulisboa.tecnico.socialsoftware.tournament.services.local.TournamentProvidedService;

public class UpdateTournamentActivitiesImpl {

    private final TournamentProvidedService tournamentService;

    public UpdateTournamentActivitiesImpl(TournamentProvidedService tournamentService) {
        this.tournamentService = tournamentService;
    }

}
