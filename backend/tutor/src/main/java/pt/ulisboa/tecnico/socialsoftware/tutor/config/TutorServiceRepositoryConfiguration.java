package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pt.ulisboa.tecnico.socialsoftware.common.config.CommonModuleConfiguration;

/**
 * The configuration class to enable Spring JPA.
 */
@Configuration
@EnableJpaRepositories
@EnableAutoConfiguration
public class TutorServiceRepositoryConfiguration {
}
