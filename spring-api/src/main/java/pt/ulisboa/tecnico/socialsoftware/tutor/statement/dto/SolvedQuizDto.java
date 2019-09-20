package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SolvedQuizDto implements Serializable {
    private StatementQuizDto statementQuiz;
    private List<ResultAnswerDto> answers = new ArrayList<>();
    private List<CorrectAnswerDto> correctAnswers = new ArrayList<>();
    private String answerDate;

    public SolvedQuizDto(){
    }

    public SolvedQuizDto(QuizAnswer quizAnswer) {
        this.statementQuiz = new StatementQuizDto(quizAnswer);
        this.answers = quizAnswer.getQuestionAnswers().stream()
                .sorted(Comparator.comparing(QuestionAnswer::getSequence))
                .map(questionAnswer -> {
                    Integer optionId = (questionAnswer.getOption() != null) ? questionAnswer.getOption().getId() : null;
                    return new ResultAnswerDto(questionAnswer.getQuizQuestion().getId(), optionId, questionAnswer.getTimeTaken());
                })
                .collect(Collectors.toList());
        this.correctAnswers = quizAnswer.getQuiz()
                .getQuizQuestions().stream()
                .sorted(Comparator.comparing(QuizQuestion::getSequence))
                .map(CorrectAnswerDto::new)
                .collect(Collectors.toList());
        this.answerDate = quizAnswer.getAnswerDate().toString();
    }

    public StatementQuizDto getStatementQuiz() {
        return statementQuiz;
    }

    public void setStatementQuiz(StatementQuizDto statementQuiz) {
        this.statementQuiz = statementQuiz;
    }

    public List<ResultAnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<ResultAnswerDto> answers) {
        this.answers = answers;
    }

    public List<CorrectAnswerDto> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(List<CorrectAnswerDto> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public String getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(String answerDate) {
        this.answerDate = answerDate;
    }
}