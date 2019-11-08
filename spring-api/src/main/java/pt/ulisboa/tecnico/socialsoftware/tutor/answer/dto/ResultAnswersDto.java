package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ResultAnswersDto implements Serializable {
    private Integer quizAnswerId;
    private List<ResultAnswerDto> answers = new ArrayList<>();
    private LocalDateTime answerDate;

    public Integer getQuizAnswerId() {
        return quizAnswerId;
    }

    public void setQuizAnswerId(Integer quizAnswerId) {
        this.quizAnswerId = quizAnswerId;
    }

    public List<ResultAnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<ResultAnswerDto> answers) {
        this.answers = answers;
    }

    public LocalDateTime getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = answerDate;
    }

    @Override
    public String toString() {
        return "ResultAnswersDto{" +
                "quizAnswerId=" + quizAnswerId +
                ", answers=" + answers +
                ", answerDate=" + answerDate +
                '}';
    }
}
