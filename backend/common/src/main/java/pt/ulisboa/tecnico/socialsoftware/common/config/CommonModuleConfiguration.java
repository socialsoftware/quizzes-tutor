package pt.ulisboa.tecnico.socialsoftware.common.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "pt.ulisboa.tecnico.socialsoftware.common")
@EntityScan({"pt.ulisboa.tecnico.socialsoftware.common"})
public class CommonModuleConfiguration {
}
