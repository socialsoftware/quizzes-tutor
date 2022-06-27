package pt.ulisboa.tecnico.socialsoftware.tournament;

import io.eventuate.tram.spring.jdbckafka.TramJdbcKafkaConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.worker.Worker;
import com.uber.cadence.worker.WorkerFactory;

import pt.ulisboa.tecnico.socialsoftware.common.config.CommonModuleConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tournament.activity.createTournamentActivities.CreateTournamentActivitiesImpl;
import pt.ulisboa.tecnico.socialsoftware.tournament.activity.removeTournamentActivities.RemoveTournamentActivitiesImpl;
import pt.ulisboa.tecnico.socialsoftware.tournament.activity.updateTournamentActivities.UpdateTournamentActivitiesImpl;
import pt.ulisboa.tecnico.socialsoftware.tournament.config.TournamentServiceWebConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tournament.demoutils.TournamentDemoUtils;
import pt.ulisboa.tecnico.socialsoftware.tournament.services.local.TournamentProvidedService;
import pt.ulisboa.tecnico.socialsoftware.tournament.subscriptions.TournamentServiceEventConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tournament.workflow.createTournament.CreateTournamentWorkflowImpl;
import pt.ulisboa.tecnico.socialsoftware.tournament.workflow.removeTournament.RemoveTournamentWorkflowImpl;
import pt.ulisboa.tecnico.socialsoftware.tournament.workflow.updateTournament.UpdateTournamentWorkflowImpl;
import pt.ulisboa.tecnico.socialsoftware.common.utils.Constants;

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

    @Autowired
    TournamentProvidedService tournamentProvidedService;

    public static void main(String[] args) {
        SpringApplication.run(TournamentApplication.class, args);
    }

    @Autowired
    private TournamentDemoUtils tournamentDemoUtils;

    @Override
    public void afterPropertiesSet() {
        tournamentDemoUtils.resetDemoInfo();
    }

    @Bean
    CommandLineRunner commandLineRunner(WorkflowClient workflowClient) {
        return args -> {
            WorkerFactory factory = WorkerFactory.newInstance(workflowClient);
            Worker worker = factory.newWorker(Constants.TOURNAMENT_TASK_LIST);
            worker.registerActivitiesImplementations(new CreateTournamentActivitiesImpl(tournamentProvidedService),
                    new RemoveTournamentActivitiesImpl(tournamentProvidedService),
                    new UpdateTournamentActivitiesImpl(tournamentProvidedService));
            worker.registerWorkflowImplementationTypes(CreateTournamentWorkflowImpl.class,
                    RemoveTournamentWorkflowImpl.class, UpdateTournamentWorkflowImpl.class);
            factory.start();
        };
    }
}
