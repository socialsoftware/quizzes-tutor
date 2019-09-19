package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

public class JwtAuthenticationDto {
    private String token;
    private String userRole;

    public JwtAuthenticationDto(String token, String userRole) {
        this.token = token;
        this.userRole = userRole;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUser(String userRole) {
        this.userRole = userRole;
    }
}