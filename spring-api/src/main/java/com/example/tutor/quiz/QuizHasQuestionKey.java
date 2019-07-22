package com.example.tutor.quiz;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class QuizHasQuestionKey implements Serializable {

    @Column(name = "quiz_id")
    private Integer quiz_id;

    @Column(name = "question_id")
    private Integer question_id;

    public Integer getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(Integer quiz_id) {
        this.quiz_id = quiz_id;
    }

    public Integer getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(Integer question_id) {
        this.question_id = question_id;
    }
}