package pt.ulisboa.tecnico.socialsoftware.common.dtos.auth;

import java.io.Serializable;

public class AuthDto implements Serializable {
    private String token;
    private AuthUserDto user;

    public AuthDto() {
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