package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;


public class SolvedQuestionDto implements Serializable {

    private Integer quizQuestionId;
    private String content;
    private List<StatementOptionDto> options;
    private ImageDto image;

    public SolvedQuestionDto(QuizQuestion quizQuestion) {
        this.quizQuestionId = quizQuestion.getId();
        this.content = quizQuestion.getQuestion().getContent();
        if (quizQuestion.getQuestion().getImage() != null) {
            this.image = new ImageDto(quizQuestion.getQuestion().getImage());
        }
        this.options = quizQuestion.getQuestion().getOptions().stream().map(StatementOptionDto::new).collect(Collectors.toList());
    }

    public Integer getQuizQuestionId() {
        return quizQuestionId;
    }

    public void setQuizQuestionId(Integer quizQuestionId) {
        this.quizQuestionId = quizQuestionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<StatementOptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<StatementOptionDto> options) {
        this.options = options;
    }

    public ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
    }
}