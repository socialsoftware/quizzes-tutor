package pt.ulisboa.tecnico.socialsoftware.tutor;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.ImpExpService;


@PropertySource({ "classpath:application.properties" })
@EnableJpaRepositories
@EnableTransactionManagement
@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
public class TutorApplication extends SpringBootServletInitializer implements InitializingBean {
    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired(required = false)
    ImpExpService impExpService;

    public static void main(String[] args) {
        SpringApplication.run(TutorApplication.class, args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!profile.equals("test")) {
            // impExpService.importAll();
        }
    }

}