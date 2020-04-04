package pt.ulisboa.tecnico.socialsoftware.tutor;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


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

    @Override
    public void afterPropertiesSet() throws Exception {
        // Run on startup
    }
}