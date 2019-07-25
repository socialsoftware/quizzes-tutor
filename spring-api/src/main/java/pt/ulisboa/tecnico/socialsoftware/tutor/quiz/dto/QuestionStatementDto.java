package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;


public class QuestionStatementDto implements Serializable {

    private Integer id;
    private String content;
    private List<OptionStatementDto> options;
    private ImageDto image;

    public QuestionStatementDto(Question question) {
        this.id = question.getId();
        this.content = question.getContent();
        if (question.getImage() != null) {
            this.image = new ImageDto(question.getImage());
        }
        this.options = question.getOptions().stream().map(OptionStatementDto::new).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<OptionStatementDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionStatementDto> options) {
        this.options = options;
    }

    public ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
    }
}