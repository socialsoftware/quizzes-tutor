package pt.ulisboa.tecnico.socialsoftware.tutor.stats.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.stats.dto.AnsweredQuestionDto;

import java.time.LocalDateTime;

public class AnsweredQuizDto {

    private LocalDateTime answerDate;
    private AnsweredQuestionDto[] questions;

    public LocalDateTime getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = answerDate;
    }

    public AnsweredQuestionDto[] getQuestions() {
        return questions;
    }

    public void setQuestions(AnsweredQuestionDto[] questions) {
        this.questions = questions;
    }
}
