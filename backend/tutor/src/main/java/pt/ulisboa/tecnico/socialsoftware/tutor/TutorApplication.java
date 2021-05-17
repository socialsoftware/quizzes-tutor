package pt.ulisboa.tecnico.socialsoftware.tutor;

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
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.TutorServiceParticipantConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.TutorServiceRepositoryConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.demoutils.TutorDemoUtils;

@PropertySource({"classpath:application.properties" })
@EnableScheduling
@EnableJpaAuditing
//@ServiceDescription(description = "Manages Orders", capabilities = "Order Management")
@SpringBootApplication
@Import({ TutorServiceParticipantConfiguration.class,
        TutorServiceRepositoryConfiguration.class, TramJdbcKafkaConfiguration.class})
@EntityScan({"pt.ulisboa.tecnico.socialsoftware.tutor"})
@EnableJpaRepositories({"pt.ulisboa.tecnico.socialsoftware.tutor"})
//@ComponentScan(basePackages = "pt.ulisboa.tecnico.socialsoftware.tournament")
public class TutorApplication implements InitializingBean {

    public static void main(String[] args) {
        SpringApplication.run(TutorApplication.class, args);
    }

    @Autowired
    private TutorDemoUtils tutorDemoUtils;

    @Autowired
    private AnswerService answerService;

    @Override
    public void afterPropertiesSet() {
        answerService.writeQuizAnswersAndCalculateStatistics();
        tutorDemoUtils.resetDemoInfo();
    }
}
