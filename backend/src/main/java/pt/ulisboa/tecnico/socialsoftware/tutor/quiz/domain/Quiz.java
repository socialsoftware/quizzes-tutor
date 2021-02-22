package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(
        name = "quizzes",
        indexes = {
                @Index(name = "quizzes_indx_0", columnList = "course_execution_id")
        })
public class Quiz implements DomainEntity {
    public enum QuizType {
        EXAM, TEST, GENERATED, PROPOSED, IN_CLASS, TOURNAMENT
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    private Integer key;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "available_date")
    private LocalDateTime availableDate;

    @Column(name = "conclusion_date")
    private LocalDateTime conclusionDate;

    @Column(name = "results_date")
    private LocalDateTime resultsDate;

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
    private final List<QuizQuestion> quizQuestions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quiz", fetch = FetchType.LAZY)
    private final Set<QuizAnswer> quizAnswers = new HashSet<>();

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    @OneToOne
    private Tournament tournament;

    public Quiz() {}

    public Quiz(QuizDto quizDto) {
        checkQuestionsSequence(quizDto.getQuestions());

        if (quizDto.getType() != null)
            setType(quizDto.getType());
        else if (quizDto.isTimed())
            setType(QuizType.IN_CLASS.toString());
        else
            setType(QuizType.PROPOSED.toString());

        setKey(quizDto.getKey());
        setTitle(quizDto.getTitle());
        setScramble(quizDto.isScramble());
        setQrCodeOnly(quizDto.isQrCodeOnly());
        setOneWay(quizDto.isOneWay());
        setCreationDate(DateHandler.toLocalDateTime(quizDto.getCreationDate()));
        setAvailableDate(DateHandler.toLocalDateTime(quizDto.getAvailableDate()));
        setConclusionDate(DateHandler.toLocalDateTime(quizDto.getConclusionDate()));
        setResultsDate(DateHandler.toLocalDateTime(quizDto.getResultsDate()));
        setSeries(quizDto.getSeries());
        setVersion(quizDto.getVersion());


    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuiz(this);
    }

    public Integer getId() {
    return id;
    }

    public Integer getKey() {
        return key;
    }

    public Integer getNonNullKey() {
        if (this.key == null)
            generateKeys();

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

    public boolean isTournamentQuiz() { return type == QuizType.TOURNAMENT; }

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
        if (title == null || title.isBlank())
            throw new TutorException(INVALID_TITLE_FOR_QUIZ);

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
        if (availableDate == null) {
            throw new TutorException(INVALID_AVAILABLE_DATE_FOR_QUIZ);
        }
        if (this.conclusionDate != null && conclusionDate.isBefore(availableDate)) {
            throw new TutorException(INVALID_AVAILABLE_DATE_FOR_QUIZ);
        }

        this.availableDate = availableDate;
    }

    public LocalDateTime getConclusionDate() {
    return conclusionDate;
    }

    public void setConclusionDate(LocalDateTime conclusionDate) {
        if (conclusionDate != null && conclusionDate.isBefore(availableDate)) {
            throw new TutorException(INVALID_CONCLUSION_DATE_FOR_QUIZ);
        }

        if (conclusionDate == null && type.equals(QuizType.IN_CLASS)) {
            throw new TutorException(INVALID_CONCLUSION_DATE_FOR_QUIZ);
        }

        this.conclusionDate = conclusionDate;
    }

    public LocalDateTime getResultsDate() {
        if (resultsDate == null)
            return conclusionDate;
        return resultsDate;
    }

    public void setResultsDate(LocalDateTime resultsDate) {
        if (resultsDate != null && resultsDate.isBefore(availableDate)) {
            throw new TutorException(INVALID_RESULTS_DATE_FOR_QUIZ);
        }
        if (resultsDate != null && conclusionDate != null && resultsDate.isBefore(conclusionDate)) {
            throw new TutorException(INVALID_RESULTS_DATE_FOR_QUIZ);
        }

        this.resultsDate = resultsDate;
    }

    public QuizType getType() {
        return type;
    }

    public void setType(String type) {
        if (type == null)
            throw new TutorException(INVALID_TYPE_FOR_QUIZ);

        try {
            this.type = QuizType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new TutorException(INVALID_TYPE_FOR_QUIZ);
        }
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

    public List<QuizQuestion> getQuizQuestions() {
        return this.quizQuestions.stream().sorted(Comparator.comparing(QuizQuestion::getSequence)).collect(Collectors.toList());
    }

    public int getQuizQuestionsNumber() {
        return this.quizQuestions.size();
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

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public void addQuizQuestion(QuizQuestion quizQuestion) {
        this.quizQuestions.add(quizQuestion);
    }

    public void removeQuizQuestion(QuizQuestion quizQuestion) {
        this.quizQuestions.remove(quizQuestion);
    }

    public void addQuizAnswer(QuizAnswer quizAnswer) {
        this.quizAnswers.add(quizAnswer);
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", key=" + key +
                ", creationDate=" + creationDate +
                ", availableDate=" + availableDate +
                ", conclusionDate=" + conclusionDate +
                ", resultsDate=" + resultsDate +
                ", scramble=" + scramble +
                ", qrCodeOnly=" + qrCodeOnly +
                ", oneWay=" + oneWay +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", series=" + series +
                ", version='" + version + '\'' +
                ", quizQuestions=" + quizQuestions +
                '}';
    }

    private void generateKeys() {
        int max = this.courseExecution.getQuizzes().stream()
                .filter(quiz -> quiz.key != null)
                .map(Quiz::getKey)
                .max(Comparator.comparing(Integer::valueOf))
                .orElse(0);

        List<Quiz> nullKeyQuizzes = this.courseExecution.getQuizzes().stream()
                .filter(quiz -> quiz.key == null).collect(Collectors.toList());

        for (Quiz quiz: nullKeyQuizzes) {
            max = max + 1;
            quiz.key = max;
        }
    }

    private void checkQuestionsSequence(List<QuestionDto> questions) {
        if (questions != null) {
            for (QuestionDto questionDto : questions) {
                if (questionDto.getSequence() != questions.indexOf(questionDto) + 1) {
                    throw new TutorException(INVALID_QUESTION_SEQUENCE_FOR_QUIZ);
                }
            }
        }
    }

    public void remove() {
        checkCanChange();

        if (this.tournament != null) {
            throw new TutorException(QUIZ_HAS_TOURNAMENT);
        }

        this.courseExecution.getQuizzes().remove(this);
        this.courseExecution = null;
    }

    public void checkCanChange() {
        if (!quizAnswers.isEmpty()) {
            throw new TutorException(QUIZ_HAS_ANSWERS);
        }
        this.quizQuestions.forEach(QuizQuestion::checkCanRemove);
    }

    public void generateQuiz(List<Question> questions) {
        IntStream.range(0,questions.size())
                .forEach(index -> new QuizQuestion(this, questions.get(index), index));

        setAvailableDate(DateHandler.now());
        setCreationDate(DateHandler.now());
        setType(QuizType.GENERATED.toString());
        setTitle("Generated Quiz");
    }
}