package pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto;

import java.io.Serializable;
import java.util.List;

public class QuizFraudGraphScoreDto implements Serializable{
    public List<QuizFraudScoreDto> scoresIn;
    public List<QuizFraudScoreDto> scoresOut;

    public QuizFraudGraphScoreDto(){}

    public QuizFraudGraphScoreDto(List<QuizFraudScoreDto> scoresIn, List<QuizFraudScoreDto>  scoresOut){
        this.scoresIn = scoresIn;
        this.scoresOut = scoresOut;
    }

    @Override
    public String toString() {
        return "QuizFraudGraphScoreDto{}";
    }
}
