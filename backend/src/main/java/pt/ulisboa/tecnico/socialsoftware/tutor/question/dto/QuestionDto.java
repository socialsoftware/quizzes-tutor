package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    private String creationDate = null;
    private String status;
    private List<OptionDto> options = new ArrayList<>();
    private ImageDto image;
    private List<TopicDto> topics = new ArrayList<>();
    private Integer sequence;

    public QuestionDto() {
    }

    public QuestionDto(Question question) {
        this.id = question.getId();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.difficulty = question.getDifficulty();
        this.numberOfAnswers = question.getNumberOfAnswers();
        if (!question.getQuizQuestions().isEmpty()) {
            this.numberOfGeneratedQuizzes = (int) question.getQuizQuestions().stream()
                    .map(QuizQuestion::getQuiz)
                    .filter(quiz -> quiz.getType().equals(Quiz.QuizType.GENERATED))
                    .count();
        }
        this.numberOfNonGeneratedQuizzes = question.getQuizQuestions().size() - this.numberOfGeneratedQuizzes;
        this.numberOfCorrect = question.getNumberOfCorrect();
        this.status = question.getStatus().name();
        this.options = question.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
        this.topics = question.getTopics().stream().sorted(Comparator.comparing(Topic::getName)).map(TopicDto::new).collect(Collectors.toList());

        if (question.getImage() != null)
            this.image = new ImageDto(question.getImage());
        if (question.getCreationDate() != null)
            this.creationDate = question.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
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
                ", options=" + options +
                ", image=" + image +
                ", topics=" + topics +
                ", sequence=" + sequence +
                '}';
    }
}