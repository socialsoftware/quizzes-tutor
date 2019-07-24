package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.io.Serializable;

public class CorrectAnswerDto implements Serializable {

    private Integer question_id;
    private Integer sequence;
    private Integer correct_option;

    public CorrectAnswerDto(Question question) {
        this.question_id = question.getId();
        this.correct_option = question.getCorrectOption();
    }

    public Integer getQuestionId() {
        return question_id;
    }

    public void setQuestionId(Integer question_id) {
        this.question_id = question_id;
    }

    public Integer getCorrectOption() {
        return correct_option;
    }

    public void setCorrectOption(Integer correct_option) {
        this.correct_option = correct_option;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}