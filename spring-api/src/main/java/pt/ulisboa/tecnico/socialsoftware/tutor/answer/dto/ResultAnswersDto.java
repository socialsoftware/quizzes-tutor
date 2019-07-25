package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;


public class ResultAnswersDto implements Serializable {
    private Integer quizAnswerId;
    private LocalDateTime answerDate;
    private Map<Integer, ResultAnswerDto> answers;

    public ResultAnswersDto(){

    }

    ResultAnswersDto(Map<Integer, ResultAnswerDto> answers) {
        this.answers = answers;
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

    public Map<Integer, ResultAnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Integer, ResultAnswerDto> answers) {
        this.answers = answers;
    }
}
