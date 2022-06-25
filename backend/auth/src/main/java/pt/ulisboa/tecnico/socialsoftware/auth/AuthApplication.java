package pt.ulisboa.tecnico.socialsoftware.auth;

import io.eventuate.tram.spring.jdbckafka.TramJdbcKafkaConfiguration;

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

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowClientOptions;
import com.uber.cadence.serviceclient.ClientOptions;
import com.uber.cadence.serviceclient.IWorkflowService;
import com.uber.cadence.serviceclient.WorkflowServiceTChannel;
import com.uber.cadence.worker.Worker;
import com.uber.cadence.worker.WorkerFactory;

import pt.ulisboa.tecnico.socialsoftware.auth.activity.confirmRegistration.ConfirmRegistrationActivitiesImpl;
import pt.ulisboa.tecnico.socialsoftware.auth.activity.createUserWithAuth.CreateUserWithAuthActivitiesImpl;
import pt.ulisboa.tecnico.socialsoftware.auth.activity.updateCourseExecutions.UpdateCourseExecutionsActivitiesImpl;
import pt.ulisboa.tecnico.socialsoftware.auth.config.AuthServiceParticipantConfiguration;
import pt.ulisboa.tecnico.socialsoftware.auth.config.AuthServiceWebConfiguration;
import pt.ulisboa.tecnico.socialsoftware.auth.services.local.AuthUserProvidedService;
import pt.ulisboa.tecnico.socialsoftware.auth.subscriptions.AuthUserServiceEventConfiguration;
import pt.ulisboa.tecnico.socialsoftware.auth.workflow.confirmRegistration.ConfirmRegistrationWorkflow;
import pt.ulisboa.tecnico.socialsoftware.auth.workflow.createUserWithAuth.CreateUserWithAuthWorkflow;
import pt.ulisboa.tecnico.socialsoftware.auth.workflow.updateCourseExecutions.UpdateCourseExectionsWorkflow;
import pt.ulisboa.tecnico.socialsoftware.common.config.CommonModuleConfiguration;
import pt.ulisboa.tecnico.socialsoftware.common.utils.Constants;

@PropertySource({ "classpath:application.properties" })
@EnableJpaAuditing
@SpringBootApplication
@Import({ CommonModuleConfiguration.class, AuthServiceWebConfiguration.class, AuthServiceParticipantConfiguration.class,
        TramJdbcKafkaConfiguration.class, AuthUserServiceEventConfiguration.class })
@EntityScan({ "pt.ulisboa.tecnico.socialsoftware.auth" })
@EnableJpaRepositories({ "pt.ulisboa.tecnico.socialsoftware.auth" })
public class AuthApplication {

    @Autowired
    private AuthUserProvidedService authUserProvidedService;

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

    @Bean
    WorkflowClient workflowClient() {
        IWorkflowService service = new WorkflowServiceTChannel(ClientOptions.defaultInstance());

        WorkflowClientOptions workflowClientOptions = WorkflowClientOptions.newBuilder()
                .setDomain(Constants.DOMAIN)
                .build();
        return WorkflowClient.newInstance(service, workflowClientOptions);
    }

    @Bean
    CommandLineRunner commandLineRunner(WorkflowClient workflowClient) {
        return args -> {
            WorkerFactory factory = WorkerFactory.newInstance(workflowClient);
            Worker worker = factory.newWorker(Constants.AUTH_TASK_LIST);
            worker.registerActivitiesImplementations(new CreateUserWithAuthActivitiesImpl(authUserProvidedService),
                    new ConfirmRegistrationActivitiesImpl(authUserProvidedService),
                    new UpdateCourseExecutionsActivitiesImpl(authUserProvidedService));
            worker.registerWorkflowImplementationTypes(CreateUserWithAuthWorkflow.class,
                    ConfirmRegistrationWorkflow.class, UpdateCourseExectionsWorkflow.class);
            factory.start();
        };
    }
}
