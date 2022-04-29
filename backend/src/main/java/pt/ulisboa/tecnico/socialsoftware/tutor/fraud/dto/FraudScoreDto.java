package pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto;

import java.io.Serializable;


public class FraudScoreDto implements Serializable {
    private UserFraudInfoDto userInfo;

    private Float score;

    public FraudScoreDto() {
    }

    public FraudScoreDto(UserFraudInfoDto userInfo, Float score) {
        this.userInfo = userInfo;
        this.score = score;
    }

    public UserFraudInfoDto getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserFraudInfoDto userInfo) {
        this.userInfo = userInfo;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "FraudScoreDto{" +
                "userInfo=" + userInfo +
                ", score=" + score +
                '}';
    }
}
