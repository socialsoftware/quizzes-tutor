package pt.ulisboa.tecnico.socialsoftware.apigateway;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pt.ulisboa.tecnico.socialsoftware.tournament.config.TournamentModuleConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.JwtTokenProvider;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.TutorModuleConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.demoutils.DemoUtils;
import pt.ulisboa.tecnico.socialsoftware.utils.config.UtilsModuleConfiguration;


@PropertySource({"classpath:application.properties" })
@EnableJpaRepositories({"pt.ulisboa.tecnico.socialsoftware.tutor","pt.ulisboa.tecnico.socialsoftware.tournament"})
@EnableTransactionManagement
@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
@EntityScan({"pt.ulisboa.tecnico.socialsoftware.tutor","pt.ulisboa.tecnico.socialsoftware.tournament", "pt.ulisboa.tecnico.socialsoftware.exceptions"})
@Import({TutorModuleConfiguration.class, TournamentModuleConfiguration.class, UtilsModuleConfiguration.class})
public class TutorApplication extends SpringBootServletInitializer implements InitializingBean {
    public static void main(String[] args) {
        SpringApplication.run(TutorApplication.class, args);
    }

    @Autowired
    private AnswerService answerService;

    @Autowired
    private DemoUtils demoUtils;

    @Override
    public void afterPropertiesSet() {
        // Run on startup
        JwtTokenProvider.generateKeys();
        answerService.writeQuizAnswersAndCalculateStatistics();

        demoUtils.resetDemoInfo();

        //EventBusFactory.getEventBus().register(new TournamentSubscriptions());
    }

}