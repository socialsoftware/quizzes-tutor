package pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto;

import java.io.Serializable;
import java.util.List;

public class QuizFraudCommunicationScoreDto implements Serializable {
    private List<FraudScoreDto> scoresIn;

    private List<FraudScoreDto> scoresOut;

    public QuizFraudCommunicationScoreDto() {
    }

    public QuizFraudCommunicationScoreDto(List<FraudScoreDto> scoresIn, List<FraudScoreDto> scoresOut) {
        this.scoresIn = scoresIn;
        this.scoresOut = scoresOut;
    }

    public List<FraudScoreDto> getScoresIn() {
        return scoresIn;
    }

    public void setScoresIn(List<FraudScoreDto> scoresIn) {
        this.scoresIn = scoresIn;
    }

    public List<FraudScoreDto> getScoresOut() {
        return scoresOut;
    }

    public void setScoresOut(List<FraudScoreDto> scoresOut) {
        this.scoresOut = scoresOut;
    }

    @Override
    public String toString() {
        return "QuizFraudCommunicationScoreDto{" +
                "scoresIn=" + scoresIn +
                ", scoresOut=" + scoresOut +
                '}';
    }
}
