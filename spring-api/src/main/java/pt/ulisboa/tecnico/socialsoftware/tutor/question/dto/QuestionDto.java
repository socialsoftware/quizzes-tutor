package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class QuestionDto implements Serializable {
    private Integer id;
    private Integer number;
    private String title;
    private String content;
    private Integer difficulty;
    private int numberOfAnswers;
    private int numberOfCorrect;
    private String status;
    private List<OptionDto> options = new ArrayList<>();
    private ImageDto image;
    private List<TopicDto> topics = new ArrayList<>();
    private Integer sequence;

    public QuestionDto() {
    }

    public QuestionDto(Question question) {
        this.id = question.getId();
        this.number = question.getNumber();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.difficulty = question.getDifficulty();
        this.numberOfAnswers = question.getNumberOfAnswers();
        this.numberOfCorrect = question.getNumberOfCorrect();
        this.status = question.getStatus().name();
        if (question.getImage() != null) {
            this.image = new ImageDto(question.getImage());
        }
        this.options = question.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
        this.topics = question.getTopics().stream().sorted(Comparator.comparing(Topic::getName)).map(TopicDto::new).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<TopicDto> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicDto> topics) {
        this.topics = topics;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "QuestionDto{" +
                "id=" + id +
                ", number=" + number +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", difficulty=" + difficulty +
                ", numberOfAnswers=" + numberOfAnswers +
                ", numberOfCorrect=" + numberOfCorrect +
                ", status='" + status + '\'' +
                ", options=" + options +
                ", image=" + image +
                ", topics=" + topics +
                ", sequence=" + sequence +
                '}';
    }
}