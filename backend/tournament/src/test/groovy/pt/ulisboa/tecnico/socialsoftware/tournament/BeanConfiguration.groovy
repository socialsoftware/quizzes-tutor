package pt.ulisboa.tecnico.socialsoftware.tournament

import com.google.common.eventbus.EventBus
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import pt.ulisboa.tecnico.socialsoftware.tournament.services.local.TournamentProvidedService
import pt.ulisboa.tecnico.socialsoftware.tournament.services.remote.TournamentRequiredService

@TestConfiguration
@PropertySource("classpath:application-test.properties")
class BeanConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }

    @Bean
    TournamentProvidedService TournamentProvidedService() {
        return new TournamentProvidedService()
    }

    @Bean
    TournamentRequiredService TournamentRequiredService() {
        return new TournamentRequiredService()
    }

    @Bean
    EventBus eventBus() {
        return new EventBus()
    }
}