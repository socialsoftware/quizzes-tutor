package com.example.tutor.stats;

import java.time.LocalDateTime;

public class AnsweredQuizDTO {

    private LocalDateTime answerDate;
    private AnsweredQuestionDTO[] questions;

    public LocalDateTime getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = answerDate;
    }

    public AnsweredQuestionDTO[] getQuestions() {
        return questions;
    }

    public void setQuestions(AnsweredQuestionDTO[] questions) {
        this.questions = questions;
    }
}
