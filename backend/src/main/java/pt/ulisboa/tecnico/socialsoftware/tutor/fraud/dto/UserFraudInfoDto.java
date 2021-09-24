package pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto;

public class UserFraudInfoDto {
    public String username;
    public String name;
    public String user_id;

    public UserFraudInfoDto() {
    }

    public UserFraudInfoDto(String username, String name, String user_id) {
        this.username = username;
        this.name = name;
        this.user_id = user_id;
    }

    public String toString() {
        return "UserFraudInfoDto{" + "username='" + username + "\'" + ", name='" + name + "\'" + ", user_id='" + user_id
                + "\'" + "}";
    }
}
