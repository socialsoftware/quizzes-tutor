package pt.ulisboa.tecnico.socialsoftware.common.events.answer;

import io.eventuate.tram.events.common.DomainEvent;

public class ExternalQuizSolvedEvent implements DomainEvent {

    private Integer quizId;
    private Integer participantId;
    private Integer numberOfAnswered;
    private Integer numberOfCorrect;

    public ExternalQuizSolvedEvent() {
    }

    public ExternalQuizSolvedEvent(Integer quizId, Integer participantId, long numberOfAnswered, long numberOfCorrect) {
        this.quizId = quizId;
        this.participantId = participantId;
        this.numberOfAnswered = Math.toIntExact(numberOfAnswered);
        this.numberOfCorrect = Math.toIntExact(numberOfCorrect);
    }

    public Integer getQuizId() {
        return quizId;
    }

    public Integer getParticipantId() {
        return participantId;
    }

    public Integer getNumberOfAnswered() {
        return numberOfAnswered;
    }

    public Integer getNumberOfCorrect() {
        return numberOfCorrect;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public void setParticipantId(Integer participantId) {
        this.participantId = participantId;
    }

    public void setNumberOfAnswered(Integer numberOfAnswered) {
        this.numberOfAnswered = numberOfAnswered;
    }

    public void setNumberOfCorrect(Integer numberOfCorrect) {
        this.numberOfCorrect = numberOfCorrect;
    }
}
