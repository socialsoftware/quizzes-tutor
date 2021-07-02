package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(TramEventsPublisherConfiguration.class)
public class TutorEventPublisherConfiguration {
}
