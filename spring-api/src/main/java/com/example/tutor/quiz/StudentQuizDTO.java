package com.example.tutor.quiz;

import com.example.tutor.question.QuestionDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StudentQuizDTO implements Serializable {
    private Integer id;
    private List<QuestionDTO> questions = new ArrayList<>();

    public StudentQuizDTO(){

    }

    public StudentQuizDTO(Quiz quiz) {
        this.id = quiz.getId();
        this.questions = quiz.getQuestions().stream().map(QuestionDTO::new).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }
}