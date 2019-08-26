package pt.ulisboa.tecnico.socialsoftware.tutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@PropertySource({ "classpath:application.properties" })
@EnableJpaRepositories
@EnableTransactionManagement
@EnableJpaAuditing
@SpringBootApplication
public class TutorApplication {
    public static void main(String[] args) {
        SpringApplication.run(TutorApplication.class, args);
    }
}