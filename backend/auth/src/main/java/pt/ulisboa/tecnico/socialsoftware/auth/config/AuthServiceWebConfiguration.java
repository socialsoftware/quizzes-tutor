package pt.ulisboa.tecnico.socialsoftware.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.eventuate.common.json.mapper.JSonMapper;
import org.springframework.context.annotation.*;

/**
 * The configuration class of external APIs.
 */
@Configuration
@ComponentScan
@Import(AuthServiceRepositoryConfiguration.class)
public class AuthServiceWebConfiguration {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JSonMapper.objectMapper;
    }

}
