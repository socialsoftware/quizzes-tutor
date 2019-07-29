package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;


public class QuestionDto implements Serializable {
    private Integer id;
    private String content;
    private Integer difficulty;
    private Boolean active;
    private List<OptionDto> options;
    private ImageDto image;
    private String title;

    public QuestionDto() {

    }

    public QuestionDto(Question question) {
        this.id = question.getId();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.difficulty = question.getDifficulty();
        this.active = question.getActive();
        if (question.getImage() != null) {
            this.image = new ImageDto(question.getImage());
        }
        this.options = question.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
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

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }

    public ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}