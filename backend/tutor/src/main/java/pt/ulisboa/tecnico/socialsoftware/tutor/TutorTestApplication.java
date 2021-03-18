package pt.ulisboa.tecnico.socialsoftware.tutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.TutorModuleConfiguration;

@Configuration
@EnableJpaRepositories({"pt.ulisboa.tecnico.socialsoftware.tutor"})
@EnableTransactionManagement
@EnableScheduling
@EntityScan({"pt.ulisboa.tecnico.socialsoftware.tutor", "pt.ulisboa.tecnico.socialsoftware.exceptions"})
@SpringBootApplication
@Import({TutorModuleConfiguration.class/*, UtilsModuleConfiguration.class*/})
public class TutorTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TutorTestApplication.class, args);
    }
}