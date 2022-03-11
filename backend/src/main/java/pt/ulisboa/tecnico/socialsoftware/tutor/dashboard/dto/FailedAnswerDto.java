package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import java.io.Serializable;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.FailedAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

public class FailedAnswerDto implements Serializable {

    private Integer id;

    private boolean answered;

    private String collected;

    private QuestionAnswerDto questionAnswerDto;

    public FailedAnswerDto(){
    }

    public FailedAnswerDto(FailedAnswer failedAnswer){
        setId(failedAnswer.getId());
        setAnswered(failedAnswer.getAnswered());
        setCollected(DateHandler.toISOString(failedAnswer.getCollected()));
        setQuestionAnswerDto(new QuestionAnswerDto(failedAnswer.getQuestionAnswer()));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean getAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public String getCollected() {
        return collected;
    }

    public void setCollected(String collected) {
        this.collected = collected;
    }

    public QuestionAnswerDto getQuestionAnswerDto() {
        return questionAnswerDto;
    }

    public void setQuestionAnswerDto(QuestionAnswerDto questionAnswerDto) {
        this.questionAnswerDto = questionAnswerDto;
    }

    @Override
    public String toString() {
        return "FailedAnswerDto{" +
            "id=" + id +
            ", answered=" + answered +
            "}";
    }
}
