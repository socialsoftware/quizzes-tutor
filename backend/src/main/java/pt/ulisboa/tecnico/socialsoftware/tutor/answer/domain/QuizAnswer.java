package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_QUIZ_ANSWER_SEQUENCE;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_ALREADY_COMPLETED;

@Entity
@Table(name = "quiz_answers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"quiz_id", "user_id"})
        },
        indexes = {
                @Index(name = "quiz_answers_indx_0", columnList = "user_id"),
                @Index(name = "quiz_answers_indx_1", columnList = "quiz_id"),
                @Index(name = "quiz_answers_indx_2", columnList = "user_id, quiz_id"),
                @Index(name = "quiz_answers_indx_3", columnList = "quiz_id, user_id")
        })
public class QuizAnswer implements DomainEntity {
    private static final Logger logger = LoggerFactory.getLogger(QuizAnswer.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "answer_date")
    private LocalDateTime answerDate;

    private boolean completed;

    private Boolean fraud = false;

    @Column(columnDefinition = "boolean default false")
    private boolean usedInStatistics;

    private Integer currentSequenceQuestion = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quizAnswer", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<QuestionAnswer> questionAnswers = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "sequence_question_id_mapping")
    @MapKeyColumn(name = "sequence")
    @Column(name = "question_id")
    private Map<Integer, Integer> sequenceQuestionIdMap = new HashMap<>();

    public QuizAnswer() {
    }

    public QuizAnswer(Student student, Quiz quiz) {
        setCompleted(false);
        setFraud(false);
        setUsedInStatistics(false);
        setStudent(student);
        setQuiz(quiz);

        List<QuizQuestion> quizQuestions = new ArrayList<>(quiz.getQuizQuestions());
        if (quiz.getScramble()) {
            Collections.shuffle(quizQuestions);
        }

        for (int i = 0; i < quizQuestions.size(); i++) {
            new QuestionAnswer(this, quizQuestions.get(i), i);

            if (quiz.isOneWay()) {
                this.sequenceQuestionIdMap.put(i, quizQuestions.get(i).getQuestion().getId());
            }
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuizAnswer(this);
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = answerDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isFraud() {
        return this.fraud != null && this.fraud;
    }

    public void setFraud(boolean fraud) {
        this.fraud = fraud;
    }

    public boolean isUsedInStatistics() {
        return usedInStatistics;
    }

    public void setUsedInStatistics(boolean usedInStatistics) {
        this.usedInStatistics = usedInStatistics;
    }

    public Integer getCurrentSequenceQuestion() {
        return currentSequenceQuestion;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
        student.addQuizAnswer(this);
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
        quiz.addQuizAnswer(this);
    }

    public void setQuestionAnswers(List<QuestionAnswer> questionAnswers) {
        this.questionAnswers = questionAnswers;
    }

    public List<QuestionAnswer> getQuestionAnswers() {
        if (questionAnswers == null) {
            questionAnswers = new ArrayList<>();
        }
        return questionAnswers;
    }

    public void addQuestionAnswer(QuestionAnswer questionAnswer) {
        if (questionAnswers == null) {
            questionAnswers = new ArrayList<>();
        }
        questionAnswers.add(questionAnswer);
    }

    public boolean canResultsBePublic() {
        return isCompleted() &&
                ((!getQuiz().getType().equals(Quiz.QuizType.IN_CLASS) && !getQuiz().getType().equals(Quiz.QuizType.TOURNAMENT))
                        || getQuiz().getResultsDate().isBefore(DateHandler.now()));
    }

    public void remove() {
        student.getQuizAnswers().remove(this);
        student = null;

        quiz.getQuizAnswers().remove(this);
        quiz = null;

        questionAnswers.forEach(QuestionAnswer::remove);
    }

    public boolean openToAnswer() {
        return !isCompleted();
    }

    public long getNumberOfAnsweredQuestions() {
        return getQuestionAnswers().stream().filter(QuestionAnswer::isAnswered).count();
    }

    public long getNumberOfCorrectAnswers() {
        return getQuestionAnswers().stream().filter(QuestionAnswer::isCorrect).count();
    }

    public void calculateQuestionStatistics() {
        if (!usedInStatistics) {
            getQuestionAnswers().forEach(questionAnswer ->
                    questionAnswer.getQuizQuestion().getQuestion().addAnswerStatistics(questionAnswer)
            );

            usedInStatistics = true;
        }
    }

    public Integer checkCorrectSequenceQuestion(Integer sequence) {
        if (sequence.equals(currentSequenceQuestion + 1)) {
            currentSequenceQuestion = sequence;
        }

        return this.sequenceQuestionIdMap.get(currentSequenceQuestion);
    }

    public void checkIsCurrentQuestion(Integer questionId) {
        Integer currentSequenceQuestionId = this.sequenceQuestionIdMap.get(currentSequenceQuestion);
        if (currentSequenceQuestionId != null && !currentSequenceQuestionId.equals(questionId)) {
            throw new TutorException(INVALID_QUIZ_ANSWER_SEQUENCE, getStudent().getUsername() + " tried to submit question with ID "
                    + questionId + " when they sequence is " + (currentSequenceQuestion + 1)
                    + " and the question IDs sequence is " +
                    getQuestionAnswers().stream()
                            .sorted(Comparator.comparing(QuestionAnswer::getSequence))
                            .map(questionAnswer -> questionAnswer.getQuestion().getId())
                            .map(String::valueOf).collect(Collectors.joining(", ")));
        } else if (completed) {
            throw new TutorException(QUIZ_ALREADY_COMPLETED);
        }
    }
}
