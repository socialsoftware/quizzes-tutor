package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuestionDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.TopicConjunction;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "questions")
public class Question implements DomainEntity {
    public enum Status {
        DISABLED, REMOVED, AVAILABLE, SUBMITTED
    }

    public static class QuestionTypes {
        public static final String MULTIPLE_CHOICE_QUESTION = "multiple_choice";
        public static final String CODE_FILL_IN_QUESTION = "code_fill_in";
        public static final String CODE_ORDER_QUESTION = "code_order";
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

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "question", fetch = FetchType.EAGER, orphanRemoval = true)
    private Image image;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "question", orphanRemoval = true)
    private QuestionDetails questionDetails;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question", fetch = FetchType.LAZY, orphanRemoval = true)
    private final Set<QuizQuestion> quizQuestions = new HashSet<>();

    @ManyToMany(mappedBy = "questions")
    private final Set<Topic> topics = new HashSet<>();


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question", fetch = FetchType.LAZY, orphanRemoval = true)
    private final Set<Discussion> discussions = new HashSet<>();

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

        setQuestionDetails(questionDto.getQuestionDetailsDto().getQuestionDetails(this));
    }

    public Integer getId() {
        return id;
    }

    public Integer getKey() {
        if (this.key == null)
            generateKeys();

        return this.key;
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

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<Discussion> getDiscussions() {
        return discussions;
    }

    public void addDiscussion(Discussion discussion) {
        discussions.add(discussion);
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

    public boolean hasTopics(Set<Integer> chosenTopicsIds) {
        return !getTopics().isEmpty()
                && getTopics().stream().map(Topic::getId).collect(Collectors.toSet()).containsAll(chosenTopicsIds);
    }

    public void update(QuestionDto questionDto) {
        if (getQuizQuestions().stream().flatMap(quizQuestion -> quizQuestion.getQuestionAnswers().stream()).findAny().isPresent()) {
            throw new TutorException(CANNOT_CHANGE_ANSWERED_QUESTION);
        }

        setTitle(questionDto.getTitle());
        setContent(questionDto.getContent());

        getQuestionDetails().update(questionDto.getQuestionDetailsDto());
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

        this.course.getQuestions().remove(this);
        course = null;

        this.topics.forEach(topic -> topic.getQuestions().remove(this));
        this.topics.clear();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestion(this);
    }

    public QuestionDetails getQuestionDetails() {
        return questionDetails;
    }

    public void setQuestionDetails(QuestionDetails question) {
        this.questionDetails = question;
        if (this.questionDetails != null) {
            this.questionDetails.setQuestion(this);
        }
    }

    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return this.questionDetails.getCorrectAnswerDetailsDto();
    }

    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return this.questionDetails.getStatementQuestionDetailsDto();
    }

    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return this.getQuestionDetails().getEmptyAnswerDetailsDto();
    }

    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return this.questionDetails.getEmptyStatementAnswerDetailsDto();
    }

    public QuestionDetailsDto getQuestionDetailsDto() {
        return this.getQuestionDetails().getQuestionDetailsDto();
    }

    public String getCorrectAnswerRepresentation() {
        return this.questionDetails.getCorrectAnswerRepresentation();
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
                ", creationDate=" + creationDate +
                ", image=" + image +
                ", quizQuestions=" + quizQuestions +
                ", topics=" + topics +
                ", course=" + course +
                ", question=" + questionDetails +
                '}';
    }

    public boolean isInSubmission() {
        return status == Status.SUBMITTED;
    }
}