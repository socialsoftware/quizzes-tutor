package pt.ulisboa.tecnico.socialsoftware.tournament;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.TutorModuleConfiguration;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableJpaRepositories({"pt.ulisboa.tecnico.socialsoftware.tutor", "pt.ulisboa.tecnico.socialsoftware.tournament"})
@EntityScan({"pt.ulisboa.tecnico.socialsoftware.tutor", "pt.ulisboa.tecnico.socialsoftware.tournament"})
//@Import(TutorModuleConfiguration.class)*/
public class TournamentTestConfiguration {
}
