package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


public class ResultAnswersDto implements Serializable {
    private Integer quizAnswerId;
    private LocalDateTime answerDate;
    private List<ResultAnswerDto> answers;

    public ResultAnswersDto(){
    }

    public Integer getQuizAnswerId() {
        return quizAnswerId;
    }

    public void setQuizAnswerId(Integer quizAnswerId) {
        this.quizAnswerId = quizAnswerId;
    }

    public LocalDateTime getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = answerDate;
    }

    public List<ResultAnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<ResultAnswerDto> answers) {
        this.answers = answers;
    }
}
