package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.AuthUserDto;

import java.io.Serializable;

public class AuthenticationResponseDto implements Serializable {
    private String token;
    private AuthUserDto user;

    public AuthenticationResponseDto(String token, AuthUserDto user) {
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