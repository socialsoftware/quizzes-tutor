package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;


public class ResultAnswersDto implements Serializable {

    private Integer quiz_id;
    private LocalDateTime answer_date;
    private Map<Integer, ResultAnswerDto> answers;

    public ResultAnswersDto(){

    }

    ResultAnswersDto(Map<Integer, ResultAnswerDto> answers) {
        this.answers = answers;
    }

    public Integer getQuizId() {
        return quiz_id;
    }

    public void setQuizId(Integer quiz_id) {
        this.quiz_id = quiz_id;
    }

    public LocalDateTime getAnswerDate() {
        return answer_date;
    }

    public void setAnswerDate(LocalDateTime answer_date) {
        this.answer_date = answer_date;
    }

    public Map<Integer, ResultAnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Integer, ResultAnswerDto> answers) {
        this.answers = answers;
    }
}
