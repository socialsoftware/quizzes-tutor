package pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto;

import java.io.Serializable;

public class QuizFraudScoreDto implements Serializable{
    public Integer username;
    public Float score;

    public QuizFraudScoreDto(){}

    public QuizFraudScoreDto(Integer username, Float score){
        this.username = username;
        this.score = score;
    }

    @Override
    public String toString() {
        return "QuizFraudScoreDto{" +
                "username=" + username +
                ", score=" + score +
                '}';
    }
}
