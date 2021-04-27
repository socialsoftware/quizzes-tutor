package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pt.ulisboa.tecnico.socialsoftware.common.config.CommonModuleConfiguration;

@Configuration
@ComponentScan(basePackages = {"pt.ulisboa.tecnico.socialsoftware.tutor"})
@EntityScan({"pt.ulisboa.tecnico.socialsoftware.tutor"})
@EnableJpaRepositories({"pt.ulisboa.tecnico.socialsoftware.tutor"})
@Import({CommonModuleConfiguration.class})
public class TutorModuleConfiguration {
}
