package pt.ulisboa.tecnico.socialsoftware.tutor.dtos.question;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionDto implements Serializable {
    private Integer id;
    private Integer key;
    private String title;
    private String content;
    private Integer difficulty;
    private int numberOfAnswers = 0;
    private int numberOfGeneratedQuizzes = 0;
    private int numberOfNonGeneratedQuizzes = 0;
    private int numberOfCorrect;
    private String creationDate;
    private String status;
    private ImageDto image;
    private List<TopicDto> topics = new ArrayList<>();
    private Integer sequence;
    private QuestionDetailsDto questionDetailsDto;

    public QuestionDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getNumberOfGeneratedQuizzes() {
        return numberOfGeneratedQuizzes;
    }

    public void setNumberOfGeneratedQuizzes(int numberOfGeneratedQuizzes) {
        this.numberOfGeneratedQuizzes = numberOfGeneratedQuizzes;
    }

    public int getNumberOfNonGeneratedQuizzes() {
        return numberOfNonGeneratedQuizzes;
    }

    public void setNumberOfNonGeneratedQuizzes(int numberOfNonGeneratedQuizzes) {
        this.numberOfNonGeneratedQuizzes = numberOfNonGeneratedQuizzes;
    }

    public int getNumberOfCorrect() {
        return numberOfCorrect;
    }

    public void setNumberOfCorrect(int numberOfCorrect) {
        this.numberOfCorrect = numberOfCorrect;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
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

    public QuestionDetailsDto getQuestionDetailsDto() {
        return questionDetailsDto;
    }

    public void setQuestionDetailsDto(QuestionDetailsDto questionDetails) {
        this.questionDetailsDto = questionDetails;
    }

    @Override
    public String toString() {
        return "QuestionDto{" +
                "id=" + id +
                ", key=" + key +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", difficulty=" + difficulty +
                ", numberOfAnswers=" + numberOfAnswers +
                ", numberOfGeneratedQuizzes=" + numberOfGeneratedQuizzes +
                ", numberOfNonGeneratedQuizzes=" + numberOfNonGeneratedQuizzes +
                ", numberOfCorrect=" + numberOfCorrect +
                ", creationDate='" + creationDate + '\'' +
                ", status='" + status + '\'' +
                ", image=" + image +
                ", topics=" + topics +
                ", sequence=" + sequence +
                ", question=" + questionDetailsDto +
                '}';
    }
}