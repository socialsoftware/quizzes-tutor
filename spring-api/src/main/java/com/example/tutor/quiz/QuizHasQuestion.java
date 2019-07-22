package com.example.tutor.quiz;

import com.example.tutor.question.Question;

import javax.persistence.*;

@Entity
@Table(name="quiz_has_question")
public class QuizHasQuestion {
    @EmbeddedId
    private QuizHasQuestionKey id;

    @ManyToOne
    @MapsId("quiz_id")
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne
    @MapsId("question_id")
    @JoinColumn(name = "question_id")
    private Question question;

    private Integer sequence;

    public QuizHasQuestion(){

    }

    public QuizHasQuestion(Quiz quiz, Question question, Integer sequence) {
        this.id = new QuizHasQuestionKey();
        this.quiz = quiz;
        this.question = question;
        this.sequence = sequence;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}