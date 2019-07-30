package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class QuizStatementDto implements Serializable {
    private Integer quizAnswerId;
    private List<QuestionStatementDto> questions = new ArrayList<>();

    public QuizStatementDto(){
    }

    public QuizStatementDto(QuizAnswer quizAnswer) {
        this.quizAnswerId = quizAnswer.getId();
        this.questions = quizAnswer.getQuiz().getQuizQuestions().stream()
                .sorted(Comparator.comparing(QuizQuestion::getSequence))
                .map(quizQuestion -> new QuestionStatementDto(quizQuestion))
                .collect(Collectors.toList());
    }

    public Integer getQuizAnswerId() {
        return quizAnswerId;
    }

    public void setQuizAnswerId(Integer quizAnswerId) {
        this.quizAnswerId = quizAnswerId;
    }

    public List<QuestionStatementDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionStatementDto> questions) {
        this.questions = questions;
    }
}