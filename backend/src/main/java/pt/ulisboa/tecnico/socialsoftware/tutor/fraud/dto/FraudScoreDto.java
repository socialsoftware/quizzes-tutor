package pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto;

import java.io.Serializable;


public class FraudScoreDto implements Serializable {
    public UserFraudInfoDto userInfo;
    public Float score;

    public FraudScoreDto() {
    }

    public FraudScoreDto(UserFraudInfoDto userInfo, Float score) {
        this.userInfo = userInfo;
        this.score = score;
    }

    @Override
    public String toString() {
        return "FraudScoreDto{" + "userInfo=" + this.userInfo.toString() + ", score=" + score + '}';
    }
}
