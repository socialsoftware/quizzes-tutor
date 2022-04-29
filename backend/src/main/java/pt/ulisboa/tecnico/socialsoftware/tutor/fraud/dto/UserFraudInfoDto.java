package pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto;

import java.io.Serializable;

public class UserFraudInfoDto implements Serializable {
    private String username;

    private String name;

    private String user_id;

    public UserFraudInfoDto() {
    }

    public UserFraudInfoDto(String username, String name, String user_id) {
        this.username = username;
        this.name = name;
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "UserFraudInfoDto{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
