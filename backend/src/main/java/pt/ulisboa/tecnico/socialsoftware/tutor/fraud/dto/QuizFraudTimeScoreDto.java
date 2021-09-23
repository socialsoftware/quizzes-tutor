package pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto;

import java.io.Serializable;
import java.util.List;

public class QuizFraudTimeScoreDto implements Serializable {

    public List<FraudScoreDto> scores;

    public QuizFraudTimeScoreDto() {
    }

    public QuizFraudTimeScoreDto(List<FraudScoreDto> scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "QuizFraudTimeScoreDto{scores:" + this.scores.toString() + +'}';
    }
}
