package pt.ulisboa.tecnico.socialsoftware.tournament;

import io.eventuate.tram.spring.jdbckafka.TramJdbcKafkaConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import pt.ulisboa.tecnico.socialsoftware.common.config.CommonModuleConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tournament.config.TournamentServiceWebConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tournament.demoutils.TournamentDemoUtils;
import pt.ulisboa.tecnico.socialsoftware.tournament.subscriptions.TournamentServiceEventConfiguration;

@PropertySource({ "classpath:application.properties" })
@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
@Import({ CommonModuleConfiguration.class, TournamentServiceWebConfiguration.class,
        TramJdbcKafkaConfiguration.class, TournamentServiceEventConfiguration.class })
@EntityScan({ "pt.ulisboa.tecnico.socialsoftware.tournament" })
@EnableJpaRepositories({ "pt.ulisboa.tecnico.socialsoftware.tournament" })
// @ServiceDescription(description = "Tournament", capabilities = "Tournament")
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
