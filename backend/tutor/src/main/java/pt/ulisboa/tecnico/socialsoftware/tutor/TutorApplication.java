package pt.ulisboa.tecnico.socialsoftware.tutor;

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
import com.uber.cadence.client.WorkflowClientOptions;
import com.uber.cadence.serviceclient.ClientOptions;
import com.uber.cadence.serviceclient.IWorkflowService;
import com.uber.cadence.serviceclient.WorkflowServiceTChannel;
import com.uber.cadence.worker.Worker;
import com.uber.cadence.worker.WorkerFactory;

import pt.ulisboa.tecnico.socialsoftware.common.config.CommonModuleConfiguration;
import pt.ulisboa.tecnico.socialsoftware.common.utils.Constants;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.activity.AnswerActivitiesImpl;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.TutorEventPublisherConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.TutorServiceWebConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.demoutils.TutorDemoUtils;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.activity.CourseExecutionActivitiesImpl;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.activity.QuestionActivitiesImpl;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.activity.QuizActivitiesImpl;

@PropertySource({ "classpath:application.properties" })
@EnableScheduling
@EnableJpaAuditing
// @ServiceDescription(description = "Manages Orders", capabilities = "Order
// Management")
@SpringBootApplication
@Import({ TramJdbcKafkaConfiguration.class, CommonModuleConfiguration.class,
        TutorServiceWebConfiguration.class, TutorEventPublisherConfiguration.class })
@EntityScan({ "pt.ulisboa.tecnico.socialsoftware.tutor" })
@EnableJpaRepositories({ "pt.ulisboa.tecnico.socialsoftware.tutor" })
public class TutorApplication implements InitializingBean {

    public static void main(String[] args) {
        SpringApplication.run(TutorApplication.class, args);
    }

    @Autowired
    private TutorDemoUtils tutorDemoUtils;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private QuizService quizService;

    @Override
    public void afterPropertiesSet() {
        answerService.writeQuizAnswersAndCalculateStatistics();
        tutorDemoUtils.resetDemoInfo();
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
            Worker worker = factory.newWorker(Constants.TUTOR_TASK_LIST);
            worker.registerActivitiesImplementations(new AnswerActivitiesImpl(answerService),
                    new CourseExecutionActivitiesImpl(courseExecutionService),
                    new QuestionActivitiesImpl(topicService), new QuizActivitiesImpl(quizService));
            worker.registerWorkflowImplementationTypes();
            factory.start();
        };
    }
}
