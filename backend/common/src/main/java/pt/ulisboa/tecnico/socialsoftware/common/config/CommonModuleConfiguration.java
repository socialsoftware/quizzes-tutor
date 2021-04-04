package pt.ulisboa.tecnico.socialsoftware.common.config;

import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "pt.ulisboa.tecnico.socialsoftware.common")
public class CommonModuleConfiguration {

    @Bean
    public EventBus eventBus() {
        return new EventBus();
    }

}
