package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuizAnswersDto implements Serializable {
    private Long secondsToSubmission;
    private List<Integer> correctSequence;
    private List<QuizAnswerDto> quizAnswers = new ArrayList<>();

    public Long getSecondsToSubmission() {
        return secondsToSubmission;
    }

    public void setSecondsToSubmission(Long secondsToSubmission) {
        this.secondsToSubmission = secondsToSubmission;
    }

    public List<Integer> getCorrectSequence() {
        return correctSequence;
    }

    public void setCorrectSequence(List<Integer> correctSequence) {
        this.correctSequence = correctSequence;
    }

    public List<QuizAnswerDto> getQuizAnswers() {
        return quizAnswers;
    }

    public void setQuizAnswers(List<QuizAnswerDto> quizAnswers) {
        this.quizAnswers = quizAnswers;
    }
}
