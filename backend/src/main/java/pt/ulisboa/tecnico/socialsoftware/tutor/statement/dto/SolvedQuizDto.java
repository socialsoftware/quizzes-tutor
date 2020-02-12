package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SolvedQuizDto implements Serializable {
    private StatementQuizDto statementQuiz;
    private List<QuestionAnswerDto> answers = new ArrayList<>();
    private List<CorrectAnswerDto> correctAnswers = new ArrayList<>();
    private String answerDate;

    public SolvedQuizDto(){
    }

    public SolvedQuizDto(QuizAnswer quizAnswer) {
        this.statementQuiz = new StatementQuizDto(quizAnswer);
        this.answers = quizAnswer.getQuestionAnswers().stream()
                .map(QuestionAnswerDto::new)
                .collect(Collectors.toList());

        this.correctAnswers = quizAnswer.getQuestionAnswers().stream()
                .map(QuestionAnswer::getQuizQuestion)
                .map(CorrectAnswerDto::new)
                .collect(Collectors.toList());

        this.answerDate = quizAnswer.getAnswerDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public StatementQuizDto getStatementQuiz() {
        return statementQuiz;
    }

    public void setStatementQuiz(StatementQuizDto statementQuiz) {
        this.statementQuiz = statementQuiz;
    }

    public List<QuestionAnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuestionAnswerDto> answers) {
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

    @Override
    public String toString() {
        return "SolvedQuizDto{" +
                "statementQuiz=" + statementQuiz +
                ", answers=" + answers +
                ", correctAnswers=" + correctAnswers +
                ", answerDate='" + answerDate + '\'' +
                '}';
    }
}