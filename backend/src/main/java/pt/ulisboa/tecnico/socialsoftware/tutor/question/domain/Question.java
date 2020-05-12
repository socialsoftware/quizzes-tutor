package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_type",
        columnDefinition = "smallint not null default 0",
        discriminatorType = DiscriminatorType.INTEGER)
public abstract class Question implements DomainEntity {
    public static class QuestionTypes{
        public static final String MultipleChoice = "0";
        public static final String CodeFillIn = "1";
    }

    public enum Status {
        DISABLED, REMOVED, AVAILABLE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    protected Integer key;

    @Column(columnDefinition = "TEXT")
    protected String content;

    @Column(nullable = false)
    protected String title;

    @Column(name = "number_of_answers", columnDefinition = "integer default 0")
    protected Integer numberOfAnswers = 0;

    @Column(name = "number_of_correct", columnDefinition = "integer default 0")
    protected Integer numberOfCorrect = 0;

    @Enumerated(EnumType.STRING)
    protected Status status = Status.DISABLED;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "question")
    protected Image image;

    @Column(name = "creation_date")
    protected LocalDateTime creationDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question", orphanRemoval = true)
    protected final Set<QuizQuestion> quizQuestions = new HashSet<>();

    @ManyToMany(mappedBy = "questions")
    protected final Set<Topic> topics = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "course_id")
    protected Course course;

    public Question() {
    }

    public Question(Course course, QuestionDto questionDto) {
        setTitle(questionDto.getTitle());
        setKey(questionDto.getKey());
        setContent(questionDto.getContent());
        setStatus(Status.valueOf(questionDto.getStatus()));
        setCreationDate(DateHandler.toLocalDateTime(questionDto.getCreationDate()));
        setCourse(course);

        if (questionDto.getImage() != null)
            setImage(new Image(questionDto.getImage()));
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestion(this);
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

    private void generateKeys() {
        int max = this.course.getQuestions().stream()
                .filter(question -> question.key != null)
                .map(Question::getKey)
                .max(Comparator.comparing(Integer::valueOf))
                .orElse(0);

        List<Question> nullKeyQuestions = this.course.getQuestions().stream()
                .filter(question -> question.key == null).collect(Collectors.toList());

        for (Question question : nullKeyQuestions) {
            max = max + 1;
            question.key = max;
        }
    }

    public void addAnswerStatistics(QuestionAnswer questionAnswer) {
        numberOfAnswers++;
        if (questionAnswer.isCorrect()) {
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

    public abstract void visitDependencies(Visitor visitor);
}