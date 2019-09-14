package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import java.io.Serializable;
import java.time.LocalDateTime;


public class ResultAnswerDto implements Serializable {
    private Integer quizQuestionId;
    private Integer optionId;
    private LocalDateTime timeTaken;

    public ResultAnswerDto(){
    }

    public ResultAnswerDto(Integer quizQuestionId, Integer optionId, LocalDateTime timeTaken){
        this.quizQuestionId = quizQuestionId;
        this.optionId = optionId;
        this.timeTaken = timeTaken;
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