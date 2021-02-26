package pt.ulisboa.tecnico.socialsoftware.tutor;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.JwtTokenProvider;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DemoUtils;


@PropertySource({ "classpath:application.properties" })
@EnableJpaRepositories
@EnableTransactionManagement
@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
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
    }

}