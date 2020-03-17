package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_HAS_ANSWERS;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_NOT_CONSISTENT;

@Entity
@Table(
        name = "quizzes",
        indexes = {
                @Index(name = "quizzes_indx_0", columnList = "key")
        })
public class Quiz {
    public enum QuizType {
        EXAM, TEST, GENERATED, PROPOSED, IN_CLASS
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable = false)
    private Integer key;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "available_date")
    private LocalDateTime availableDate;

    @Column(name = "conclusion_date")
    private LocalDateTime conclusionDate;

    @Column(columnDefinition = "boolean default false")
    private boolean scramble = false;

    @Column(columnDefinition = "boolean default false")
    private boolean qrCodeOnly = false;

    @Column(columnDefinition = "boolean default false")
    private boolean oneWay = false;

    @Column(nullable = false)
    private String title = "Title";

    @Enumerated(EnumType.STRING)
    private QuizType type;

    private Integer series;
    private String version;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quiz", fetch = FetchType.LAZY, orphanRemoval=true)
    private Set<QuizQuestion> quizQuestions = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quiz", fetch = FetchType.LAZY, orphanRemoval=true)
    private Set<QuizAnswer> quizAnswers = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    public Quiz() {}

    public Quiz(QuizDto quizDto) {
        checkQuestions(quizDto.getQuestions());

        this.key = quizDto.getKey();
        setTitle(quizDto.getTitle());
        this.type = quizDto.getType();
        this.scramble = quizDto.isScramble();
        this.qrCodeOnly = quizDto.isQrCodeOnly();
        this.oneWay = quizDto.isOneWay();
        this.creationDate = quizDto.getCreationDateDate();
        setAvailableDate(quizDto.getAvailableDateDate());
        setConclusionDate(quizDto.getConclusionDateDate());
        this.series = quizDto.getSeries();
        this.version = quizDto.getVersion();
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

    public boolean getScramble() {
    return scramble;
    }

    public void setScramble(boolean scramble) {
    this.scramble = scramble;
    }

    public boolean isQrCodeOnly() {
        return qrCodeOnly;
    }

    public void setQrCodeOnly(boolean qrCodeOnly) {
        this.qrCodeOnly = qrCodeOnly;
    }

    public boolean isOneWay() {
        return oneWay;
    }

    public void setOneWay(boolean noBack) {
        this.oneWay = noBack;
    }

    public String getTitle() {
    return title;
    }

    public void setTitle(String title) {
        checkTitle(title);
        this.title = title;
    }

    public LocalDateTime getCreationDate() {
    return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
    }

    public LocalDateTime getAvailableDate() {
    return availableDate;
    }

    public void setAvailableDate(LocalDateTime availableDate) {
        checkAvailableDate(availableDate);
        this.availableDate = availableDate;
    }

    public LocalDateTime getConclusionDate() {
    return conclusionDate;
    }

    public void setConclusionDate(LocalDateTime conclusionDate) {
        checkConclusionDate(conclusionDate);
        this.conclusionDate = conclusionDate;
    }

    public QuizType getType() {
        return type;
    }

    public void setType(QuizType type) {
    this.type = type;
    }

    public Integer getSeries() {
    return series;
    }

    public void setSeries(Integer series) {
    this.series = series;
    }

    public String getVersion() {
    return version;
    }

    public void setVersion(String version) {
    this.version = version;
    }

    public Set<QuizQuestion> getQuizQuestions() {
    return quizQuestions;
    }

    public Set<QuizAnswer> getQuizAnswers() {
        return quizAnswers;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
        courseExecution.addQuiz(this);
    }

    public void addQuizQuestion(QuizQuestion quizQuestion) {
        this.quizQuestions.add(quizQuestion);
    }

    public void addQuizAnswer(QuizAnswer quizAnswer) {
        this.quizAnswers.add(quizAnswer);
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", availableDate=" + availableDate +
                ", conclusionDate=" + conclusionDate +
                ", scramble=" + scramble +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", id=" + id +
                ", series=" + series +
                ", version='" + version + '\'' +
                '}';
    }

    private void checkTitle(String title) {
        if (title == null || title.trim().length() == 0) {
            throw new TutorException(QUIZ_NOT_CONSISTENT, "Title");
        }
    }

    private void checkAvailableDate(LocalDateTime availableDate) {
        if (this.type.equals(QuizType.PROPOSED) && availableDate == null) {
            throw new TutorException(QUIZ_NOT_CONSISTENT, "Available date");
        }
        if (this.type.equals(QuizType.PROPOSED) && this.availableDate != null && this.conclusionDate != null && conclusionDate.isBefore(availableDate)) {
            throw new TutorException(QUIZ_NOT_CONSISTENT, "Available date");
        }
    }

    private void checkConclusionDate(LocalDateTime conclusionDate) {
        if (this.type.equals(QuizType.PROPOSED) &&
                conclusionDate != null &&
                availableDate != null &&
                conclusionDate.isBefore(availableDate)) {
            throw new TutorException(QUIZ_NOT_CONSISTENT, "Conclusion date " + conclusionDate + availableDate);
        }
    }

    private void checkQuestions(List<QuestionDto> questions) {
        if (questions != null) {
            for (QuestionDto questionDto : questions) {
                if (questionDto.getSequence() != questions.indexOf(questionDto) + 1) {
                    throw new TutorException(QUIZ_NOT_CONSISTENT, "sequence of questions not correct");
                }
            }
        }
    }

    public void remove() {
        checkCanChange();

        courseExecution.getQuizzes().remove(this);
        courseExecution = null;
    }

    public void checkCanChange() {
        if (!quizAnswers.isEmpty()) {
            throw new TutorException(QUIZ_HAS_ANSWERS);
        }
        getQuizQuestions().forEach(QuizQuestion::checkCanRemove);
    }

    public void generate(List<Question> questions) {
        IntStream.range(0,questions.size())
                .forEach(index -> new QuizQuestion(this, questions.get(index), index));

        this.setAvailableDate(LocalDateTime.now());
        this.setCreationDate(LocalDateTime.now());
        this.setType(QuizType.GENERATED);
        this.title = "Generated Quiz";
    }
}