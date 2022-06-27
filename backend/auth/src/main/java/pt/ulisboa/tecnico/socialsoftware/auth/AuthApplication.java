package pt.ulisboa.tecnico.socialsoftware.auth;

import io.eventuate.tram.spring.jdbckafka.TramJdbcKafkaConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import pt.ulisboa.tecnico.socialsoftware.auth.config.AuthServiceWebConfiguration;
import pt.ulisboa.tecnico.socialsoftware.auth.subscriptions.AuthUserServiceEventConfiguration;
import pt.ulisboa.tecnico.socialsoftware.common.config.CommonModuleConfiguration;

@PropertySource({ "classpath:application.properties" })
@EnableJpaAuditing
@SpringBootApplication
@Import({ CommonModuleConfiguration.class, AuthServiceWebConfiguration.class,
        TramJdbcKafkaConfiguration.class, AuthUserServiceEventConfiguration.class })
@EntityScan({ "pt.ulisboa.tecnico.socialsoftware.auth" })
@EnableJpaRepositories({ "pt.ulisboa.tecnico.socialsoftware.auth" })
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

}
