package pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto;

public class AuthPasswordDto {
    private String username;

    private String password;

    public AuthPasswordDto() {
    }

    public AuthPasswordDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
