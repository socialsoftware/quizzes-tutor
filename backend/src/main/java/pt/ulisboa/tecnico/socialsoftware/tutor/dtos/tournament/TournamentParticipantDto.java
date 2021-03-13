package pt.ulisboa.tecnico.socialsoftware.tutor.dtos.tournament;

import java.io.Serializable;

public class TournamentParticipantDto implements Serializable {
    private Integer userId;
    private String name;
    private String username;
    private boolean answered;
    private Double score;
    private Integer numberOfAnswered;
    private Integer numberOfCorrect;

    public TournamentParticipantDto() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getNumberOfAnswered() {
        return numberOfAnswered;
    }

    public void setNumberOfAnswered(Integer numberOfAnswered) {
        this.numberOfAnswered = numberOfAnswered;
    }

    public Integer getNumberOfCorrect() {
        return numberOfCorrect;
    }

    public void setNumberOfCorrect(Integer numberOfCorrect) {
        this.numberOfCorrect = numberOfCorrect;
    }
}