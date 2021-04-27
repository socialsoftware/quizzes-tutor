package pt.ulisboa.tecnico.socialsoftware.tutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.TutorModuleConfiguration;

@Configuration
@EnableAutoConfiguration
@Import({TutorModuleConfiguration.class})
public class TutorServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(TutorServiceMain.class, args);
    }
}
