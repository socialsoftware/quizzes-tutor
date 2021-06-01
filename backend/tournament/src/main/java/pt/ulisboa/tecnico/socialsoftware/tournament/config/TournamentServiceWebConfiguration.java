package pt.ulisboa.tecnico.socialsoftware.tournament.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.eventuate.common.json.mapper.JSonMapper;
import org.springframework.context.annotation.*;

/**
 * The configuration class of external APIs.
 */
@Configuration
@ComponentScan
@Import(TournamentServiceRepositoryConfiguration.class)
public class TournamentServiceWebConfiguration {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JSonMapper.objectMapper;
    }
}
