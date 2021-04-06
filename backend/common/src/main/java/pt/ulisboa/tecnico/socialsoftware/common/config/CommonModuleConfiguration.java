package pt.ulisboa.tecnico.socialsoftware.common.config;

import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pt.ulisboa.tecnico.socialsoftware.common.remote.*;

@Configuration
@ComponentScan(basePackages = "pt.ulisboa.tecnico.socialsoftware.common")
public class CommonModuleConfiguration {

    @Bean
    public EventBus eventBus() {
        return new EventBus();
    }

    @Bean
    public UserInterface userInterface() {
        return new UserInterface();
    }

    @Bean
    public CourseExecutionInterface courseExecutionInterface() {
        return new CourseExecutionInterface();
    }

    @Bean
    public QuestionInterface questionInterface() {
        return new QuestionInterface();
    }

    @Bean
    public QuizInterface quizInterface() {
        return new QuizInterface();
    }

    @Bean
    public AnswerInterface answerInterface() {
        return new AnswerInterface();
    }

}
