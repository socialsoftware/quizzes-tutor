package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;


public class QuestionDto implements Serializable {
    private Integer id;
    private String title;
    private String content;
    private double difficulty;
    private int numberOfAnswers;
    private int numberOfCorrect;
    private Boolean active;
    private List<OptionDto> options;
    private ImageDto image;
    private List<String> topics;

    public QuestionDto() {
    }

    public QuestionDto(Question question) {
        this.id = question.getId();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.difficulty = question.getDifficulty();
        this.numberOfAnswers = question.getNumberOfAnswers();
        this.numberOfCorrect = question.getNumberOfCorrect();
        this.active = question.getActive();
        if (question.getImage() != null) {
            this.image = new ImageDto(question.getImage());
        }
        this.options = question.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
        this.topics = question.getTopics().stream().map(Topic::getName).sorted().collect(Collectors.toList());
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

    public double getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public void setDifficulty(double difficulty) {
        this.difficulty = difficulty;
    }

    public int getNumberOfAnswers() {
        return numberOfAnswers;
    }

    public void setNumberOfAnswers(int numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }

    public int getNumberOfCorrect() {
        return numberOfCorrect;
    }

    public void setNumberOfCorrect(int numberOfCorrect) {
        this.numberOfCorrect = numberOfCorrect;
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

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}