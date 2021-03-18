package pt.ulisboa.tecnico.socialsoftware.tournament;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pt.ulisboa.tecnico.socialsoftware.tournament.config.TournamentModuleConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.TutorModuleConfiguration;
import pt.ulisboa.tecnico.socialsoftware.utils.config.UtilsModuleConfiguration;

@Configuration
@EnableJpaRepositories/*({"pt.ulisboa.tecnico.socialsoftware.tutor","pt.ulisboa.tecnico.socialsoftware.tournament"})*/
@EnableTransactionManagement
@EnableScheduling
@EntityScan({"pt.ulisboa.tecnico.socialsoftware.tutor","pt.ulisboa.tecnico.socialsoftware.tournament", "pt.ulisboa.tecnico.socialsoftware.exceptions"})
@SpringBootApplication
@Import({/*UtilsModuleConfiguration.class,*/ TutorModuleConfiguration.class, TournamentModuleConfiguration.class})
public class TournamentTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TournamentTestApplication.class, args);
    }
}