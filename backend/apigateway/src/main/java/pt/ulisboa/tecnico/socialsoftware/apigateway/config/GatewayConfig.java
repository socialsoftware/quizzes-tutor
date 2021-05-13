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

    private final String AUTH_SERVICE_URL = "http://localhost:8082";
    private final String TOURNAMENT_SERVICE_URL = "http://localhost:8083";
    private final String TUTOR_SERVICE_URL = "http://localhost:8084";

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("tournament-service", r -> r.path("/tournaments/**")
                        .filters(f -> f.filter(filter))
                        .uri(TOURNAMENT_SERVICE_URL))

                .route("auth-service", r -> r.path("/auth/**","/users/**")
                        .filters(f -> f.filter(filter))
                        .uri(AUTH_SERVICE_URL))

                .route("answer-service", r -> r.path("/answers/**")
                        .filters(f -> f.filter(filter))
                        .uri(TUTOR_SERVICE_URL))

                .route("discussion-service", r -> r.path("/discussions/**")
                        .filters(f -> f.filter(filter))
                        .uri(TUTOR_SERVICE_URL))

                .route("executions-service", r -> r.path("/executions","/executions/**","/assessments/**")
                        .filters(f -> f.filter(filter))
                        .uri(TUTOR_SERVICE_URL))

                .route("impexp-service", r -> r.path("/admin/**")
                        .filters(f -> f.filter(filter))
                        .uri(TUTOR_SERVICE_URL))

                .route("question-service", r -> r.path("/topics/**","/questions/**")
                        .filters(f -> f.filter(filter))
                        .uri(TUTOR_SERVICE_URL))

                .route("submissions-service", r -> r.path("/submissions/**")
                        .filters(f -> f.filter(filter))
                        .uri(TUTOR_SERVICE_URL))

                .route("quiz-service", r -> r.path("/quizzes/**")
                        .filters(f -> f.filter(filter))
                        .uri(TUTOR_SERVICE_URL))

                .route("stats-service", r -> r.path("**/stats")
                        .filters(f -> f.filter(filter))
                        .uri(TUTOR_SERVICE_URL))

                .build();
    }

}
