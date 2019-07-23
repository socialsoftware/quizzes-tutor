package com.example.tutor.answer;

import com.example.tutor.question.Question;
import com.example.tutor.quiz.QuizQuestion;
import com.example.tutor.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "Answer")
@Table(name = "answers")
public class Answer implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;

    private Integer option;

    @Column(name = "answer_date")
    private LocalDateTime date;

    @Column(columnDefinition = "time_taken")
    private LocalDateTime timeTaken;

    @ManyToOne
    @JoinColumn(name = "quiz_question_id")
    private QuizQuestion quizQuestion;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Answer() {

    }

    public Answer(ResultAnswerDTO ans, User user, Question question, LocalDateTime date, Integer quiz_id){

        this.option = ans.getOption();
    }

}