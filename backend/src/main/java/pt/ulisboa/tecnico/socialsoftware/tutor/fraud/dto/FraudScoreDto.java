package pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto;

import java.io.Serializable;

public class FraudScoreDto implements Serializable {
    public String username;
    public Float score;

    public FraudScoreDto() {
    }

    public FraudScoreDto(String username, Float score) {
        this.username = username;
        this.score = score;
    }

    @Override
    public String toString() {
        return "FraudScoreDto{" + "username=" + username + ", score=" + score + '}';
    }
}
