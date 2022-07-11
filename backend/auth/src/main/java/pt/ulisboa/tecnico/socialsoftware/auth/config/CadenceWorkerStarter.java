package pt.ulisboa.tecnico.socialsoftware.auth.config;

import com.uber.cadence.DescribeDomainResponse;
import com.uber.cadence.ListDomainsRequest;
import com.uber.cadence.ListDomainsResponse;
import com.uber.cadence.RegisterDomainRequest;
import com.uber.cadence.serviceclient.IWorkflowService;
import com.uber.cadence.worker.Worker;
import com.uber.cadence.worker.WorkerFactory;
import com.uber.cadence.worker.WorkerOptions;

import pt.ulisboa.tecnico.socialsoftware.auth.activity.confirmRegistration.ConfirmRegistrationActivitiesImpl;
import pt.ulisboa.tecnico.socialsoftware.auth.activity.createUserWithAuth.CreateUserWithAuthActivitiesImpl;
import pt.ulisboa.tecnico.socialsoftware.auth.activity.updateCourseExecutions.UpdateCourseExecutionsActivitiesImpl;
import pt.ulisboa.tecnico.socialsoftware.auth.services.local.AuthUserProvidedService;
import pt.ulisboa.tecnico.socialsoftware.auth.workflow.confirmRegistration.ConfirmRegistrationWorkflowImpl;
import pt.ulisboa.tecnico.socialsoftware.auth.workflow.createUserWithAuth.CreateUserWithAuthWorkflowImpl;
import pt.ulisboa.tecnico.socialsoftware.auth.workflow.updateCourseExecutions.UpdateCourseExectionsWorkflowImpl;
import pt.ulisboa.tecnico.socialsoftware.common.utils.CadenceConstants;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static pt.ulisboa.tecnico.socialsoftware.auth.config.CadenceConfiguration.DOMAIN;

@Component
public class CadenceWorkerStarter {

    private static final Logger logger = LoggerFactory.getLogger(CadenceWorkerStarter.class);

    @Autowired
    AuthUserProvidedService authUserProvidedService;

    private final IWorkflowService workflowService;
    private final WorkerFactory workerFactory;
    private final WorkerOptions workerOptions;

    public CadenceWorkerStarter(IWorkflowService workflowService, WorkerFactory workerFactory,
            WorkerOptions workerOptions) {
        this.workflowService = workflowService;
        this.workerFactory = workerFactory;
        this.workerOptions = workerOptions;
    }

    @PostConstruct
    public void startWorkerFactory() throws TException {
        while (true) {
            try {
                if (workflowService.isHealthy().get())
                    break;
                logger.info("Waiting 10 sec for cadence server to start up");
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            } catch (ExecutionException e) {
                e.printStackTrace();
                continue;
            }
        }

        if (!domainExists()) {
            registerDomain();
        }

        createWorkers();

        logger.info("Starting Cadence Worker Factory");
        workerFactory.start();
    }

    @PreDestroy
    public void shutdownWorkerFactory() {
        logger.info("Shutdown Cadence Worker Factory");
        workerFactory.shutdown();
    }

    private void registerDomain() throws TException {
        RegisterDomainRequest request = new RegisterDomainRequest();
        request.setDescription(DOMAIN);
        request.setEmitMetric(false);
        request.setName(DOMAIN);
        request.setWorkflowExecutionRetentionPeriodInDays(2);

        workflowService.RegisterDomain(request);
        logger.info("Successfully registered domain \"{}\"", DOMAIN);
    }

    private void createWorkers() {
        Worker worker = workerFactory.newWorker(CadenceConstants.AUTH_TASK_LIST, workerOptions);

        worker.registerActivitiesImplementations(new CreateUserWithAuthActivitiesImpl(authUserProvidedService),
                new ConfirmRegistrationActivitiesImpl(authUserProvidedService),
                new UpdateCourseExecutionsActivitiesImpl(authUserProvidedService));

        worker.registerWorkflowImplementationTypes(CreateUserWithAuthWorkflowImpl.class,
                ConfirmRegistrationWorkflowImpl.class, UpdateCourseExectionsWorkflowImpl.class);
    }

    private boolean domainExists() throws TException {
        try {
            ListDomainsRequest listDomainsRequest = new ListDomainsRequest();
            ListDomainsResponse response = workflowService.ListDomains(listDomainsRequest);
            List<DescribeDomainResponse> domains = response.getDomains();

            return domains.stream()
                    .anyMatch(d -> d.domainInfo.name.equals(DOMAIN));
        } catch (UnsupportedOperationException e) {
            logger.warn("Listing or registering domains is not supported when using a local embedded test server, " +
                    "these steps will be skipped");
            return true; // evaluate as true so domain won't be registered.
        }
    }
}
