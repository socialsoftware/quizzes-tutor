package com.example.tutor.quiz;

import com.example.tutor.question.QuestionDTO;
import com.example.tutor.question.StudentQuestionDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentQuizDTO implements Serializable {
    private Integer id;
    private Map<Integer, StudentQuestionDTO> questions = new HashMap<>();

    public StudentQuizDTO(){

    }

    public StudentQuizDTO(Quiz quiz) {
        this.id = quiz.getId();
        this.questions = quiz.getQuestions().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new StudentQuestionDTO(entry.getValue())));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Map<Integer, StudentQuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(Map<Integer, StudentQuestionDTO> questions) {
        this.questions = questions;
    }
}