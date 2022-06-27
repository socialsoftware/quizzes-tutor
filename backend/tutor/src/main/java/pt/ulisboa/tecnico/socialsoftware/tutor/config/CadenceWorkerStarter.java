package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import com.uber.cadence.DescribeDomainResponse;
import com.uber.cadence.ListDomainsRequest;
import com.uber.cadence.ListDomainsResponse;
import com.uber.cadence.RegisterDomainRequest;
import com.uber.cadence.serviceclient.IWorkflowService;
import com.uber.cadence.worker.Worker;
import com.uber.cadence.worker.WorkerFactory;
import com.uber.cadence.worker.WorkerOptions;
import pt.ulisboa.tecnico.socialsoftware.common.utils.Constants;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.activity.AnswerActivitiesImpl;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.activity.CourseExecutionActivitiesImpl;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.activity.QuestionActivitiesImpl;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.activity.QuizActivitiesImpl;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.config.CadenceConfiguration.DOMAIN;

@Component
public class CadenceWorkerStarter {

    private static final Logger logger = LoggerFactory.getLogger(CadenceWorkerStarter.class);

    @Autowired
    private AnswerService answerService;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private QuizService quizService;

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
        Worker worker = workerFactory.newWorker(Constants.TUTOR_TASK_LIST, workerOptions);

        worker.registerActivitiesImplementations(new AnswerActivitiesImpl(answerService),
                new CourseExecutionActivitiesImpl(courseExecutionService),
                new QuestionActivitiesImpl(topicService), new QuizActivitiesImpl(quizService));

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
