package pt.ulisboa.tecnico.socialsoftware.auth;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableJpaRepositories({"pt.ulisboa.tecnico.socialsoftware.tutor", "pt.ulisboa.tecnico.socialsoftware.auth"})
@EntityScan({"pt.ulisboa.tecnico.socialsoftware.tutor", "pt.ulisboa.tecnico.socialsoftware.auth"})
public class AuthTestConfiguration {
}
