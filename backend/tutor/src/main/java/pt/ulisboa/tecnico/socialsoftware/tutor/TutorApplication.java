package pt.ulisboa.tecnico.socialsoftware.tutor;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.TutorModuleConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.demoutils.TutorDemoUtils;

@PropertySource({"classpath:application.properties" })
@EnableTransactionManagement
@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@Import({TutorModuleConfiguration.class})
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
