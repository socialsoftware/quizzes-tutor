package pt.ulisboa.tecnico.socialsoftware.common.config;

import com.google.common.eventbus.EventBus;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pt.ulisboa.tecnico.socialsoftware.common.remote.*;

@Configuration
@ComponentScan(basePackages = "pt.ulisboa.tecnico.socialsoftware.common")
@EntityScan({"pt.ulisboa.tecnico.socialsoftware.common"})
public class CommonModuleConfiguration {
}
