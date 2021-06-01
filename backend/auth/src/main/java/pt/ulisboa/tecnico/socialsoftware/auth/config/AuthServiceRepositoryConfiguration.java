package pt.ulisboa.tecnico.socialsoftware.auth.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The configuration class to enable Spring JPA.
 */
@Configuration
@EnableJpaRepositories
@EnableAutoConfiguration
@Import({AuthServiceOrchestratorConfiguration.class})
public class AuthServiceRepositoryConfiguration {
}
