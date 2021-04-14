package pt.ulisboa.tecnico.socialsoftware.tutor;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableJpaRepositories({"pt.ulisboa.tecnico.socialsoftware.tutor"})
@EntityScan({"pt.ulisboa.tecnico.socialsoftware.tutor"})
public class TutorTestConfiguration {
}
