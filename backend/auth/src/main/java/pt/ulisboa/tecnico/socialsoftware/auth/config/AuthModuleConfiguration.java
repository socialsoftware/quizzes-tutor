package pt.ulisboa.tecnico.socialsoftware.auth.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pt.ulisboa.tecnico.socialsoftware.common.config.CommonModuleConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.TutorModuleConfiguration;

@Configuration
@ComponentScan(basePackages = "pt.ulisboa.tecnico.socialsoftware.auth")
@EnableJpaRepositories({"pt.ulisboa.tecnico.socialsoftware.auth"})
@EntityScan({"pt.ulisboa.tecnico.socialsoftware.auth"})
@Import({CommonModuleConfiguration.class, TutorModuleConfiguration.class})
public class AuthModuleConfiguration {
}
