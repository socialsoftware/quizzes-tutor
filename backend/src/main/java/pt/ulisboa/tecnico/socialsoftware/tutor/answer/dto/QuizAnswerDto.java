package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class QuizAnswerDto implements Serializable {
    private Integer id;
    private String name;
    private String username;
    private String creationDate;
    private String answerDate;
    private List<QuestionAnswerDto> questionAnswers = new ArrayList<>();


    public QuizAnswerDto() {
    }

    public QuizAnswerDto(QuizAnswer quizAnswer) {
        this.id = quizAnswer.getId();
        this.username = quizAnswer.getUser().getUsername();
        this.name = quizAnswer.getUser().getName();
        if (quizAnswer.getAnswerDate() != null)
            this.answerDate = quizAnswer.getAnswerDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (quizAnswer.getAnswerDate() != null)
            this.creationDate = quizAnswer.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        this.questionAnswers = quizAnswer.getQuestionAnswers().stream()
                .sorted(Comparator.comparing(qa -> qa.getQuizQuestion().getSequence()))
                .map(QuestionAnswerDto::new)
                .collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(String answerDate) {
        this.answerDate = answerDate;
    }

    public List<QuestionAnswerDto> getQuestionAnswers() {
        return questionAnswers;
    }

    public void setQuestionAnswers(List<QuestionAnswerDto> questionAnswers) {
        this.questionAnswers = questionAnswers;
    }

    public void addQuestionAnswer(QuestionAnswerDto questionAnswerDto) {
        this.questionAnswers.add(questionAnswerDto);
    }
}
