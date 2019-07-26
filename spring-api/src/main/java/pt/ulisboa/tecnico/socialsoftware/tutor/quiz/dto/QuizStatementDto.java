package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QuizStatementDto implements Serializable {
    private Integer quizAnswerId;
    private Map<Integer, QuestionStatementDto> questions = new HashMap<>();

    public QuizStatementDto(){
    }

    public QuizStatementDto(QuizAnswer quizAnswer) {
        this.quizAnswerId = quizAnswer.getId();
        this.questions = quizAnswer.getQuiz().getQuizQuestionsMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new QuestionStatementDto(entry.getValue())));
    }

    public Integer getQuizAnswerId() {
        return quizAnswerId;
    }

    public void setQuizAnswerId(Integer quizAnswerId) {
        this.quizAnswerId = quizAnswerId;
    }

    public Map<Integer, QuestionStatementDto> getQuestions() {
        return questions;
    }
}