package pt.ulisboa.tecnico.socialsoftware.tutor;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service.ImpExpService;

@PropertySource({ "classpath:application.properties", "classpath:specific.properties", "classpath:database.properties"})
@EnableJpaRepositories
@EnableTransactionManagement
@EnableJpaAuditing
@SpringBootApplication
public class TutorApplication extends SpringBootServletInitializer implements InitializingBean {
//    @Autowired
//    ImpExpService impExpService;

    public static void main(String[] args) {
        SpringApplication.run(TutorApplication.class, args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
     //   impExpService.importAll();
    }

}