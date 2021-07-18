package pt.ulisboa.tecnico.socialsoftware.tournament.demoutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tournament.services.local.TournamentProvidedService;

@Component
public class TournamentDemoUtils {

    @Autowired
    private TournamentProvidedService tournamentProvidedService;

    public void resetDemoInfo() {
        tournamentProvidedService.resetDemoTournaments();
    }
}
