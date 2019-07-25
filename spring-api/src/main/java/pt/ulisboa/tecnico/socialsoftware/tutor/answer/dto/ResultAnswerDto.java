package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;

import java.io.Serializable;
import java.time.LocalDateTime;


public class ResultAnswerDto implements Serializable {
    private Integer quizQuestionId;
    private Integer optionId;
    private LocalDateTime timeTaken;

    public ResultAnswerDto(){
    }

    public ResultAnswerDto(QuestionAnswer questionAnswer) {
        this.quizQuestionId = questionAnswer.getQuizQuestion().getQuestion().getId();
        this.optionId = questionAnswer.getOption().getId();
        this.timeTaken = questionAnswer.getTimeTaken();
    }

    public Integer getQuizQuestionId() {
        return quizQuestionId;
    }

    public void setQuizQuestionId(Integer quizQuestionId) {
        this.quizQuestionId = quizQuestionId;
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    public LocalDateTime getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(LocalDateTime timeTaken) {
        this.timeTaken = timeTaken;
    }

}