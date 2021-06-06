package pt.ulisboa.tecnico.socialsoftware.apigateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableHystrix
public class GatewayConfig {

    @Autowired
    AuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder, ServiceDestinations serviceDestinations) {
        return builder.routes()
                .route("tournament-service", r -> r.path("/tournaments/**")
                        .filters(f -> f.filter(filter))
                        .uri(serviceDestinations.getTournamentServiceUrl()))


                .route("auth-service", r -> r.path("/auth/**","/users/**")
                        .filters(f -> f.filter(filter))
                        .uri(serviceDestinations.getAuthServiceUrl()))

                .route("answer-service", r -> r.path("/answers/**")
                        .filters(f -> f.filter(filter))
                        .uri(serviceDestinations.getTutorServiceUrl()))

                .route("discussion-service", r -> r.path("/discussions/**")
                        .filters(f -> f.filter(filter))
                        .uri(serviceDestinations.getTutorServiceUrl()))

                .route("executions-service", r -> r.path("/executions","/executions/**","/assessments/**")
                        .filters(f -> f.filter(filter))
                        .uri(serviceDestinations.getTutorServiceUrl()))

                .route("impexp-service", r -> r.path("/admin/**")
                        .filters(f -> f.filter(filter))
                        .uri(serviceDestinations.getTutorServiceUrl()))

                .route("question-service", r -> r.path("/topics/**","/questions/**")
                        .filters(f -> f.filter(filter))
                        .uri(serviceDestinations.getTutorServiceUrl()))

                .route("submissions-service", r -> r.path("/submissions/**")
                        .filters(f -> f.filter(filter))
                        .uri(serviceDestinations.getTutorServiceUrl()))

                .route("quiz-service", r -> r.path("/quizzes/**")
                        .filters(f -> f.filter(filter))
                        .uri(serviceDestinations.getTutorServiceUrl()))

                .route("stats-service", r -> r.path("/stats/**")
                        .filters(f -> f.filter(filter))
                        .uri(serviceDestinations.getTutorServiceUrl()))

                .build();
    }

}
