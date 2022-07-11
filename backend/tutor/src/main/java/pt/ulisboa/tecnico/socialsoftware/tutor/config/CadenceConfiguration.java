package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowClientOptions;
import com.uber.cadence.serviceclient.ClientOptions;
import com.uber.cadence.serviceclient.IWorkflowService;
import com.uber.cadence.serviceclient.WorkflowServiceTChannel;
import com.uber.cadence.worker.WorkerFactory;
import com.uber.cadence.worker.WorkerFactoryOptions;
import com.uber.cadence.worker.WorkerOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static pt.ulisboa.tecnico.socialsoftware.common.utils.CadenceConstants.DOMAIN;

@Configuration
public class CadenceConfiguration {

    @Bean
    public WorkflowClientOptions workflowClientOptions() {
        return WorkflowClientOptions.newBuilder()
                .setDomain(DOMAIN)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(WorkflowClient.class)
    public WorkflowClient workflowClient(IWorkflowService workflowService,
            WorkflowClientOptions workflowClientOptions) {
        return WorkflowClient.newInstance(workflowService, workflowClientOptions);
    }

    @Bean
    @ConditionalOnMissingBean(ClientOptions.class)
    public ClientOptions cadenceClientOptions() {
        return ClientOptions.newBuilder()
                .setHost("172.21.0.9")
                .setPort(7933)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(IWorkflowService.class)
    public IWorkflowService cadenceClient(ClientOptions clientOptions) {
        return new WorkflowServiceTChannel(clientOptions);
    }

    @Bean
    public WorkerFactoryOptions workerFactoryOptions() {
        return WorkerFactoryOptions.newBuilder().build();
    }

    @Bean
    @ConditionalOnMissingBean(WorkerFactory.class)
    public WorkerFactory workerFactory(WorkflowClient workflowClient, WorkerFactoryOptions workerFactoryOptions) {
        return new WorkerFactory(workflowClient, workerFactoryOptions);
    }

    @Bean
    public WorkerOptions workerOptions() {
        return WorkerOptions.newBuilder().build();
    }
}
