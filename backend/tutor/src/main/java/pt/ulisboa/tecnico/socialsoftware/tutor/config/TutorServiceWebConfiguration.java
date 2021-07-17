package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.eventuate.common.json.mapper.JSonMapper;
import org.springframework.context.annotation.*;

/**
 * The configuration class of external APIs.
 */
@Configuration
@ComponentScan
@Import(TutorServiceRepositoryConfiguration.class)
public class TutorServiceWebConfiguration {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JSonMapper.objectMapper;
    }
}
