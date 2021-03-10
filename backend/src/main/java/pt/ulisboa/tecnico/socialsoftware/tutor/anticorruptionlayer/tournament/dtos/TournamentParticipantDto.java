package pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament.dtos;

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentParticipant;

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

    public TournamentParticipantDto(TournamentParticipant user) {
        //QuizAnswer quizAnswer = user.getQuizAnswer(quiz);
        this.userId = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        //this.answered = quizAnswer != null;
        this.answered = user.hasAnswered();
        //this.numberOfAnswered = quizAnswer != null ? Math.toIntExact(quizAnswer.getNumberOfAnsweredQuestions()) : 0;
        this.numberOfAnswered = user.getNumberOfAnswered();
        //this.numberOfCorrect = quizAnswer != null ? Math.toIntExact(quizAnswer.getNumberOfCorrectAnswers()) : 0;
        this.numberOfCorrect = user.getNumberOfCorrect();
        this.score = numberOfCorrect - (numberOfAnswered - numberOfCorrect) * 0.3;
    }

    public TournamentParticipantDto(Integer userId, String name, String username) {
        this.userId = userId;
        this.name = name;
        this.username = username;
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