package pt.ulisboa.tecnico.socialsoftware.apigateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "destinations")
public class ServiceDestinations {

    @NotNull
    private String authServiceUrl;

    @NotNull
    private String tournamentServiceUrl;

    @NotNull
    private String tutorServiceUrl;

    public String getAuthServiceUrl() {
        return authServiceUrl;
    }

    public void setAuthServiceUrl(String authServiceUrl) {
        this.authServiceUrl = authServiceUrl;
    }

    public String getTournamentServiceUrl() {
        return tournamentServiceUrl;
    }

    public void setTournamentServiceUrl(String tournamentServiceUrl) {
        this.tournamentServiceUrl = tournamentServiceUrl;
    }

    public String getTutorServiceUrl() {
        return tutorServiceUrl;
    }

    public void setTutorServiceUrl(String tutorServiceUrl) {
        this.tutorServiceUrl = tutorServiceUrl;
    }
}
