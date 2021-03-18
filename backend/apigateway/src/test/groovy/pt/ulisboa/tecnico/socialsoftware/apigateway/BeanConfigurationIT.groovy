package pt.ulisboa.tecnico.socialsoftware.apigateway


import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService

@TestConfiguration
@PropertySource("classpath:application-test-int.properties")
class BeanConfigurationIT {

    @Value('${spring.mail.host}')
    private String host

    @Value('${spring.mail.port}')
    private int port

    @Value('${spring.mail.username}')
    private String username

    @Value('${spring.mail.password}')
    private String password

    @Value('${spring.mail.properties.mail.smtp.auth}')
    private String auth;

    @Value('${spring.mail.properties.mail.smtp.starttls.enable}')
    private String starttls

    @Value('${spring.mail.properties.mail.transport.protocol}')
    private String protocol

    @Value('${spring.mail.properties.mail.debug}')
    private String debug

    /*@Bean
    QuizService quizService() {
        return new QuizService()
    }

    @Bean
    AnswerService answerService() {
        return new AnswerService()
    }

    @Bean
    AnswersXmlImport answersXmlImport() {
        return new AnswersXmlImport()
    }*/

    @Bean
    UserService userService() {
        return new UserService()
    }
/*
    @Bean
    UserApplicationalService userServiceApplicational() {
        return new UserApplicationalService()
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }

    @Bean
    QuestionService questionService() {
        return new QuestionService()
    }

    @Bean
    CourseExecutionService courseService() {
        return new CourseExecutionService()
    }

    @Bean
    AuthUserService authUserService() {
        return new AuthUserService()
    }

    @Bean
    TopicService topicService() {
        return new TopicService()
    }*/

    /*@Bean
    TournamentProvidedService TournamentProvidedService() {
        return new TournamentProvidedService()
    }

    @Bean
    TournamentRequiredService TournamentRequiredService() {
        return new TournamentRequiredService()
    }

    @Bean
    TournamentACL TournamentACL() {
        return new TournamentACL()
    }*/
/*
    @Bean
    AssessmentService assessmentService() {
        return new AssessmentService()
    }

    @Bean
    DiscussionService discussionService() {
        return new DiscussionService()
    }

    @Bean
    QuestionSubmissionService questionSubmissionService() {
        return new QuestionSubmissionService()
    }

    @Bean
    DemoUtils demoUtils() {
        return new DemoUtils()
    }

    @Bean
    Mailer mailer() {
        return new Mailer()
    }

    @Bean
    EventBus eventBus() {
        return new EventBus()
    }

    @Bean
    MonolithService monolithService() {
        return new MonolithService()
    }*/

    /*@Bean
    JavaMailSender getJavaMailSender() {
        JavaMailSender mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", Boolean.parseBoolean(protocol));
        props.put("mail.smtp.auth", Boolean.parseBoolean(auth));
        props.put("mail.smtp.starttls.enable", starttls);
        props.put("mail.debug", debug);

        return mailSender;
    }*/
}