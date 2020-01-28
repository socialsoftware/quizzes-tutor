package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import java.io.Serializable;
import java.util.List;

public class CorrectAnswersDto implements Serializable {
    private List<CorrectAnswerDto> answers;

    public CorrectAnswersDto(List<CorrectAnswerDto> answers) {
        this.answers = answers;
    }

    public List<CorrectAnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<CorrectAnswerDto> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "CorrectAnswersDto{" +
                "answers=" + answers +
                '}';
    }
}