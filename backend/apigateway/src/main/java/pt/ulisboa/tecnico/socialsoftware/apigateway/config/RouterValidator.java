package pt.ulisboa.tecnico.socialsoftware.apigateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
@EnableConfigurationProperties(ServiceDestinations.class)
public class RouterValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/auth/fenix",
            "/auth/external",
            "/auth/demo/student",
            "/auth/demo/teacher",
            "/auth/demo/admin",
            "/users/register/confirm"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
