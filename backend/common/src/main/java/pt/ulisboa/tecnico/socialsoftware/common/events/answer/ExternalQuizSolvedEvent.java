package pt.ulisboa.tecnico.socialsoftware.common.events.answer;

public class ExternalQuizSolvedEvent {

    private Integer quizId;
    private Integer participantId;
    private Integer numberOfAnswered;
    private Integer numberOfCorrect;

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
}
