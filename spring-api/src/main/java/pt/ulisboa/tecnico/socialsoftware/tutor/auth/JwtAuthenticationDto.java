package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import java.io.Serializable;

public class JwtAuthenticationDto implements Serializable {
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

    @Override
    public String toString() {
        return "JwtAuthenticationDto{" +
                "token='" + token + '\'' +
                ", userRole='" + userRole + '\'' +
                '}';
    }
}