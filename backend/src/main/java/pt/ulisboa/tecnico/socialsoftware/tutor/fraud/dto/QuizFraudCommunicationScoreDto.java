package pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto;

import java.io.Serializable;
import java.util.List;

public class QuizFraudCommunicationScoreDto implements Serializable {
    public List<FraudScoreDto> scoresIn;
    public List<FraudScoreDto> scoresOut;

    public QuizFraudCommunicationScoreDto() {
    }

    public QuizFraudCommunicationScoreDto(List<FraudScoreDto> scoresIn, List<FraudScoreDto> scoresOut) {
        this.scoresIn = scoresIn;
        this.scoresOut = scoresOut;
    }

    @Override
    public String toString() {
        return "QuizFraudCommunicationScoreDto{scoresIn:" + scoresIn.toString() + ", scoresOut:"
                + scoresOut.toString() + "}";
    }
}
