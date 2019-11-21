package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.*;

@Entity
@Table(
        name = "questions",
        indexes = {
                @Index(name = "question_indx_0", columnList = "number")
        })
public class Question {
    @SuppressWarnings("unused")
    public enum Status {
        DISABLED, REMOVED, AVAILABLE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "number")
    private Integer number;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String title;

    @Column(name = "number_of_answers", columnDefinition = "integer default 0")
    private Integer numberOfAnswers = 0;

    @Column(name = "number_of_correct", columnDefinition = "integer default 0")
    private Integer numberOfCorrect = 0;

    @Enumerated(EnumType.STRING)
    private Status status = Status.DISABLED;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "question")
    private Image image;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question", fetch = FetchType.EAGER)
    private List<Option> options = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question")
    private Set<QuizQuestion> quizQuestions = new HashSet<>();

    @ManyToMany(mappedBy = "questions")
    private Set<Topic> topics = new HashSet<>();

    public Question() {
    }

    public Question(QuestionDto questionDto) {
        checkConsistentQuestion(questionDto);

        this.title = questionDto.getTitle();
        this.number = questionDto.getNumber();
        this.content = questionDto.getContent();
        this.status = Status.valueOf(questionDto.getStatus());

        if (questionDto.getImage() != null) {
            Image img = new Image(questionDto.getImage());
            setImage(img);
            img.setQuestion(this);
        }

        int index = 0;
        for (OptionDto optionDto : questionDto.getOptions()) {
            optionDto.setNumber(index++);
            Option option = new Option(optionDto);
            this.options.add(option);
            option.setQuestion(this);
        }
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        image.setQuestion(this);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<QuizQuestion> getQuizQuestions() {
        return quizQuestions;
    }

    public Integer getNumberOfAnswers() {
        return numberOfAnswers;
    }

    public void setNumberOfAnswers(Integer numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }

    public Integer getNumberOfCorrect() {
        return numberOfCorrect;
    }

    public void setNumberOfCorrect(Integer numberOfCorrect) {
        this.numberOfCorrect = numberOfCorrect;
    }

    public void setQuizQuestions(Set<QuizQuestion> quizQuestions) {
        this.quizQuestions = quizQuestions;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }

    public void addOption(Option option) {
        options.add(option);
    }

    public void addQuizQuestion(QuizQuestion quizQuestion) {
        quizQuestions.add(quizQuestion);
    }

    public void addTopic(Topic topic) {
        topics.add(topic);
    }

    public void remove() {
        canRemove();
        getTopics().forEach(topic -> topic.getQuestions().remove(this));
        getTopics().clear();
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", number=" + number +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", numberOfAnswers=" + numberOfAnswers +
                ", numberOfCorrect=" + numberOfCorrect +
                ", status=" + status +
                ", image=" + image +
                ", options=" + options +
                ", topics=" + topics +
                '}';
    }

    public Integer getCorrectOptionId() {
        return this.getOptions().stream()
                .filter(Option::getCorrect)
                .findAny()
                .map(Option::getId)
                .orElse(null);
    }

    public void addAnswer(QuestionAnswer questionAnswer) {
        numberOfAnswers++;
        if (questionAnswer.getOption() != null && questionAnswer.getOption().getCorrect()) {
            numberOfCorrect++;
        }
    }


    public Integer getDifficulty() {
        // required because the import is done directely in the database
        if (numberOfAnswers == null || numberOfAnswers == 0) {
            numberOfAnswers = getQuizQuestions().stream()
                    .flatMap(quizQuestion -> quizQuestion.getQuestionAnswers().stream()).map(e -> 1).reduce(0, Integer::sum);
            numberOfCorrect = getQuizQuestions().stream()
                    .flatMap(quizQuestion -> quizQuestion.getQuestionAnswers().stream())
                    .filter(questionAnswer -> questionAnswer.getOption() != null && questionAnswer.getOption().getCorrect()).map(e -> 1).reduce(0, Integer::sum);
        }

        if (numberOfAnswers == 0) {
            return null;
        }

        return numberOfCorrect * 100 / numberOfAnswers;
    }

    public void update(QuestionDto questionDto) {
        checkConsistentQuestion(questionDto);

        setTitle(questionDto.getTitle());
        setContent(questionDto.getContent());

        questionDto.getOptions().forEach(optionDto -> {
            Option option = getOptionById(optionDto.getId());
            if (option == null) {
                throw new TutorException(OPTION_NOT_FOUND, optionDto.getId());
            }
            option.setContent(optionDto.getContent());
            option.setCorrect(optionDto.getCorrect());
        });

        // TODO: not yet implemented
        //new Image(questionDto.getImage());
    }

    private void checkConsistentQuestion(QuestionDto questionDto) {
        if (questionDto.getTitle().trim().length() == 0 ||
                questionDto.getContent().trim().length() == 0 ||
                questionDto.getOptions().stream().anyMatch(optionDto -> optionDto.getContent().trim().length() == 0)) {
            throw new TutorException(QUESTION_MISSING_DATA);
        }

        if (questionDto.getOptions().stream().filter(OptionDto::getCorrect).count() != 1) {
            throw new TutorException(QUESTION_MULTIPLE_CORRECT_OPTIONS);
        }

        if (!questionDto.getOptions().stream().filter(OptionDto::getCorrect).findAny()
                .equals(getOptions().stream().filter(Option::getCorrect).findAny().map(OptionDto::new))
                && getQuizQuestions().stream().flatMap(quizQuestion -> quizQuestion.getQuestionAnswers().stream())
                .findAny().isPresent()) {
            throw new TutorException(QUESTION_CHANGE_CORRECT_OPTION_HAS_ANSWERS);
        }

    }

    public void updateTopics(Set<Topic> newTopics) {
        Set<Topic> toRemove = this.topics.stream().filter(topic -> !newTopics.contains(topic)).collect(Collectors.toSet());

        toRemove.forEach(topic -> {
            this.topics.remove(topic);
            topic.getQuestions().remove(this);
        });

        newTopics.stream().filter(topic -> !this.topics.contains(topic)).forEach(topic -> {
            this.topics.add(topic);
            topic.getQuestions().add(this);
        });
    }

    private Option getOptionById(Integer id) {
        return getOptions().stream().filter(option -> option.getId().equals(id)).findAny().orElse(null);
    }

    private void canRemove() {
        if (!getQuizQuestions().isEmpty()) {
            throw new TutorException(QUESTION_IS_USED_IN_QUIZ, getQuizQuestions().iterator().next().getQuiz().getTitle());
        }
    }

    public void setOptionsNumber() {
        int index = 0;
        for (Option option: getOptions()) {
            option.setNumber(index++);
        }
    }

    public boolean belongsToAssessment(Assessment chosenAssessment) {
        return chosenAssessment.getTopicConjunctions().stream().map(TopicConjunction::getTopics).collect(Collectors.toList()).contains(this.topics);
    }
}