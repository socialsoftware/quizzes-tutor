package com.example.tutor.quiz;

import com.example.tutor.question.QuestionDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentQuizDTO implements Serializable {
    private Integer id;
    private Map<Integer, QuestionDTO> questions = new HashMap<>();

    public StudentQuizDTO(){

    }

    public StudentQuizDTO(Quiz quiz) {
        this.id = quiz.getId();
        this.questions = quiz.getQuestions().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new QuestionDTO(entry.getValue())));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Map<Integer, QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(Map<Integer, QuestionDTO> questions) {
        this.questions = questions;
    }
}