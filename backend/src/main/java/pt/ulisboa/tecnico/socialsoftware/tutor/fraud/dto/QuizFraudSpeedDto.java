package pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuizFraudSpeedDto implements Serializable {
    private List<UserFraudSpeedDto> users;

    public QuizFraudSpeedDto() {
    }

    public QuizFraudSpeedDto(List<UserFraudSpeedDto> users) {
        this.users = users;
    }

    public List<UserFraudSpeedDto> getUsers() {
        return users;
    }

    public void setUsers(List<UserFraudSpeedDto> users) {
        this.users = users;
    }
}
