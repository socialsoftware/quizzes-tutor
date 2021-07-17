package pt.ulisboa.tecnico.socialsoftware.tournament.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tournament.demoutils.TournamentDemoUtils;

@Component
public class ScheduledTasks {

    @Autowired
    private TournamentDemoUtils tournamentDemoUtils;

    @Scheduled(cron = "0 0 1 * * *")
    public void resetDemoInfo() {
        tournamentDemoUtils.resetDemoInfo();
    }
}
