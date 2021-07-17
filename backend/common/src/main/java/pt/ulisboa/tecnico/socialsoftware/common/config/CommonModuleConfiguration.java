package pt.ulisboa.tecnico.socialsoftware.common.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pt.ulisboa.tecnico.socialsoftware.common.remote.AnswerInterface;
import pt.ulisboa.tecnico.socialsoftware.common.remote.CourseExecutionInterface;
import pt.ulisboa.tecnico.socialsoftware.common.remote.QuestionInterface;
import pt.ulisboa.tecnico.socialsoftware.common.remote.UserInterface;

@Configuration
@ComponentScan(basePackages = "pt.ulisboa.tecnico.socialsoftware.common")
@EntityScan({"pt.ulisboa.tecnico.socialsoftware.common"})
public class CommonModuleConfiguration {

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
    public AnswerInterface answerInterface() {
        return new AnswerInterface();
    }
}
