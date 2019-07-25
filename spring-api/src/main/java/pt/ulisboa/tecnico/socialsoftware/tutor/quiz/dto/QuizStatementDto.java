package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QuizStatementDto implements Serializable {
    private Integer answerQuizId;
    private Map<Integer, QuestionStatementDto> questions = new HashMap<>();

    public QuizStatementDto(){
    }

    public QuizStatementDto(Quiz quiz) {
        this.answerQuizId = quiz.getId();
        this.questions = quiz.getQuizQuestionsMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new QuestionStatementDto(entry.getValue().getQuestion())));
    }

    public Integer getAnswerQuizId() {
        return answerQuizId;
    }

    public void setAnswerQuizId(Integer answerQuizId) {
        this.answerQuizId = answerQuizId;
    }

    public Map<Integer, QuestionStatementDto> getQuestions() {
        return questions;
    }
}