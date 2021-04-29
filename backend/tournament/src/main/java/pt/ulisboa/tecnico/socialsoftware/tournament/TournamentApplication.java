package pt.ulisboa.tecnico.socialsoftware.tournament;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pt.ulisboa.tecnico.socialsoftware.tournament.config.TournamentModuleConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tournament.demoutils.TournamentDemoUtils;

@PropertySource({"classpath:application.properties" })
@EnableTransactionManagement
@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@Import({TournamentModuleConfiguration.class})
public class TournamentApplication implements InitializingBean {

    public static void main(String[] args) {
        SpringApplication.run(TournamentApplication.class, args);
    }

    @Autowired
    private TournamentDemoUtils tournamentDemoUtils;

    @Override
    public void afterPropertiesSet() {
        tournamentDemoUtils.resetDemoInfo();
    }
}
