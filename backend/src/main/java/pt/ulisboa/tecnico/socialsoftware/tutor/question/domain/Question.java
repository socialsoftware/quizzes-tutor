package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "questions")
public class Question implements DomainEntity {
    public enum Status {
        DISABLED, REMOVED, AVAILABLE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer key;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String title;

    @Column(name = "number_of_answers", columnDefinition = "integer default 0")
    private Integer numberOfAnswers = 0;

    @Column(name = "number_of_correct", columnDefinition = "integer default 0")
    private Integer numberOfCorrect = 0;

    @Enumerated(EnumType.STRING)
    private Status status = Status.DISABLED;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "question", fetch = FetchType.EAGER, orphanRemoval=true)
    private Image image;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question", fetch = FetchType.EAGER, orphanRemoval=true)
    private final List<Option> options = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question", fetch = FetchType.LAZY, orphanRemoval=true)
    private final Set<QuizQuestion> quizQuestions = new HashSet<>();

    @ManyToMany(mappedBy = "questions")
    private final Set<Topic> topics = new HashSet<>();

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question", fetch = FetchType.LAZY, orphanRemoval=true)
    private Set<Discussion> discussions = new HashSet<>();

    public Question() {
    }

    public Question(Course course, QuestionDto questionDto) {
        setTitle(questionDto.getTitle());
        setKey(questionDto.getKey());
        setContent(questionDto.getContent());
        setStatus(Status.valueOf(questionDto.getStatus()));
        setCreationDate(DateHandler.toLocalDateTime(questionDto.getCreationDate()));
        setCourse(course);
        setOptions(questionDto.getOptions());

        if (questionDto.getImage() != null)
            setImage(new Image(questionDto.getImage()));
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestion(this);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDiscussions(Set<Discussion> discussions) {
        this.discussions = discussions;
    }

    public Integer getId() {
        return id;
    }

    public Integer getKey() {
        if (this.key == null)
            generateKeys();

        return key;
    }

   public void setKey(Integer key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (content == null || content.isBlank())
            throw new TutorException(INVALID_CONTENT_FOR_QUESTION);

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

    public void setOptions(List<OptionDto> options) {
        if (options.stream().filter(OptionDto::getCorrect).count() != 1) {
            throw new TutorException(ONE_CORRECT_OPTION_NEEDED);
        }

        int index = 0;
        for (OptionDto optionDto : options) {
            if (optionDto.getId() == null) {
                optionDto.setSequence(index++);
                new Option(optionDto).setQuestion(this);
            } else {
                Option option = getOptions()
                        .stream()
                        .filter(op -> op.getId().equals(optionDto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new TutorException(OPTION_NOT_FOUND, optionDto.getId()));

                option.setContent(optionDto.getContent());
                option.setCorrect(optionDto.getCorrect());
            }
        }
    }

    public void addOption(Option option) {
        options.add(option);
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
        if (title == null || title.isBlank())
            throw new TutorException(INVALID_TITLE_FOR_QUESTION);
        this.title = title;
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        if (this.creationDate == null) {
            this.creationDate = DateHandler.now();
        } else {
            this.creationDate = creationDate;
        }
    }

    public Set<QuizQuestion> getQuizQuestions() {
        return quizQuestions;
    }

    public void addQuizQuestion(QuizQuestion quizQuestion) {
        quizQuestions.add(quizQuestion);
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void addTopic(Topic topic) {
        topics.add(topic);
        topic.getQuestions().add(this);
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
        course.addQuestion(this);
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", key=" + key +
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

    private void generateKeys() {
        int max = this.course.getQuestions().stream()
                .filter(question -> question.key != null)
                .map(Question::getKey)
                .max(Comparator.comparing(Integer::valueOf))
                .orElse(0);

        List<Question> nullKeyQuestions = this.course.getQuestions().stream()
                .filter(question -> question.key == null).collect(Collectors.toList());

        for (Question question: nullKeyQuestions) {
            max = max + 1;
            question.key = max;
        }
    }

    public Integer getCorrectOptionId() {
        return this.getOptions().stream()
                .filter(Option::getCorrect)
                .findAny()
                .map(Option::getId)
                .orElse(null);
    }

    public void addAnswerStatistics(QuestionAnswer questionAnswer) {
        numberOfAnswers++;
        if (questionAnswer.getOption() != null && questionAnswer.getOption().getCorrect()) {
            numberOfCorrect++;
        }
    }

    public Integer getDifficulty() {
        if (numberOfAnswers == 0) {
            return null;
        }

        return numberOfCorrect * 100 / numberOfAnswers;
    }

    public boolean belongsToAssessment(Assessment chosenAssessment) {
        return chosenAssessment.getTopicConjunctions().stream().map(TopicConjunction::getTopics).collect(Collectors.toList()).contains(this.topics);
    }

    public void update(QuestionDto questionDto) {
        if (getQuizQuestions().stream().flatMap(quizQuestion -> quizQuestion.getQuestionAnswers().stream()).findAny().isPresent()) {
            throw new TutorException(CANNOT_CHANGE_ANSWERED_QUESTION);
        }

        setTitle(questionDto.getTitle());
        setContent(questionDto.getContent());
        setOptions(questionDto.getOptions());
    }

    public void updateTopics(Set<Topic> newTopics) {
        Set<Topic> toRemove = this.topics.stream().filter(topic -> !newTopics.contains(topic)).collect(Collectors.toSet());

        toRemove.forEach(topic -> {
            this.topics.remove(topic);
            topic.getQuestions().remove(this);
        });

        newTopics.stream().filter(topic -> !this.topics.contains(topic)).forEach(this::addTopic);
    }

    public void remove() {
        if (!getQuizQuestions().isEmpty()) {
            throw new TutorException(QUESTION_IS_USED_IN_QUIZ, getQuizQuestions().iterator().next().getQuiz().getTitle());
        }

        getCourse().getQuestions().remove(this);
        course = null;

        getTopics().forEach(topic -> topic.getQuestions().remove(this));
        getTopics().clear();
    }

    public void addDiscussion(Discussion discussion) {
        this.discussions.add(discussion);
    }

    public Set<Discussion> getDiscussions() {
        return this.discussions;
    }
}