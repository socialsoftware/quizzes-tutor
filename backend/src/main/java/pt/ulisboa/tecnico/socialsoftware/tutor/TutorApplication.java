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
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.JwtTokenProvider;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.AssessmentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

@PropertySource({ "classpath:application.properties" })
@EnableJpaRepositories
@EnableTransactionManagement
@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
public class TutorApplication extends SpringBootServletInitializer implements InitializingBean {
    public static void main(String[] args) {
        SpringApplication.run(TutorApplication.class, args);
    }

    @Autowired
    UserService userService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private StatementService statementService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private AssessmentService assessmentService;

    @Override
    public void afterPropertiesSet() {
        // Run on startup
        JwtTokenProvider.generateKeys();
        userService.getDemoTeacher();
        userService.getDemoStudent();
        userService.getDemoAdmin();

        quizService.resetDemoQuizzes();
        topicService.resetDemoTopics();
        assessmentService.resetDemoAssessments();
    }
}