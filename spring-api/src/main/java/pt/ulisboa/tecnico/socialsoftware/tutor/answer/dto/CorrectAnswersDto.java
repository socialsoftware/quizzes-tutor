package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import java.io.Serializable;
import java.util.Map;

public class CorrectAnswersDto implements Serializable {

    private Map<Integer, CorrectAnswerDto> answers;

    public CorrectAnswersDto(Map<Integer, CorrectAnswerDto> answers) {
        this.answers = answers;
    }

    public Map<Integer, CorrectAnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Integer, CorrectAnswerDto> answers) {
        this.answers = answers;
    }
}