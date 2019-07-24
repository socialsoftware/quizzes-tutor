package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Quiz;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentQuizDto implements Serializable {
    private Integer id;
    private Map<Integer, StudentQuestionDto> questions = new HashMap<>();

    public StudentQuizDto(){
    }

    public StudentQuizDto(Quiz quiz) {
        this.id = quiz.getId();
        this.questions = quiz.getQuizQuestions().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new StudentQuestionDto(entry.getValue().getQuestion())));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Map<Integer, StudentQuestionDto> getQuestions() {
        return questions;
    }
}