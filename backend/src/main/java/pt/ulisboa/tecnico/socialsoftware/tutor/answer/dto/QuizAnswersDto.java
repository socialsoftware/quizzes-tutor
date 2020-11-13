package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuizAnswersDto implements Serializable {
    private Long timeToSubmission;
    private List<String> correctSequence;
    private List<QuizAnswerDto> quizAnswers = new ArrayList<>();

    public Long getTimeToSubmission() {
        return timeToSubmission;
    }

    public void setTimeToSubmission(Long timeToSubmission) {
        this.timeToSubmission = timeToSubmission;
    }

    public List<String> getCorrectSequence() {
        return correctSequence;
    }

    public void setCorrectSequence(List<String> correctSequence) {
        this.correctSequence = correctSequence;
    }

    public List<QuizAnswerDto> getQuizAnswers() {
        return quizAnswers;
    }

    public void setQuizAnswers(List<QuizAnswerDto> quizAnswers) {
        this.quizAnswers = quizAnswers;
    }
}
