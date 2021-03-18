package pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto;

import java.io.Serializable;

public class AuthDto implements Serializable {
    private String token;
    private AuthUserDto user;

    public AuthDto(String token, AuthUserDto user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AuthUserDto getUser() {
        return user;
    }

    public void setUser(AuthUserDto user) {
        this.user = user;
    }
}