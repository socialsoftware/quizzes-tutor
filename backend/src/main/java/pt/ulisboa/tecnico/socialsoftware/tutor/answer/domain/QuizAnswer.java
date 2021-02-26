package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "quiz_answers",
        uniqueConstraints={
        @UniqueConstraint(columnNames = {"quiz_id", "user_id"})
        },
        indexes = {
                @Index(name = "quiz_answers_indx_0", columnList = "user_id"),
                @Index(name = "quiz_answers_indx_1", columnList = "quiz_id"),
                @Index(name = "quiz_answers_indx_2", columnList = "user_id, quiz_id"),
                @Index(name = "quiz_answers_indx_3", columnList = "quiz_id, user_id")
        })
public class QuizAnswer implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "answer_date")
    private LocalDateTime answerDate;

    private boolean completed;

    @Column(columnDefinition = "boolean default false")
    private boolean usedInStatistics;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quizAnswer", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<QuestionAnswer> questionAnswers = new ArrayList<>();

    public QuizAnswer() {
    }

    public QuizAnswer(User user, Quiz quiz) {
        setCompleted(false);
        setUsedInStatistics(false);
        setUser(user);
        setQuiz(quiz);

        List<QuizQuestion> quizQuestions = new ArrayList<>(quiz.getQuizQuestions());
        if (quiz.getScramble()) {
            Collections.shuffle(quizQuestions);
        }

        for (int i = 0; i < quizQuestions.size(); i++) {
            new QuestionAnswer(this, quizQuestions.get(i), i);
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

    public boolean isUsedInStatistics() {
        return usedInStatistics;
    }

    public void setUsedInStatistics(boolean usedInStatistics) {
        this.usedInStatistics = usedInStatistics;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        user.addQuizAnswer(this);
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

    @Override
    public String toString() {
        return "QuizAnswer{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", answerDate=" + answerDate +
                ", completed=" + completed +
                ", usedInStatistics=" + usedInStatistics +
                '}';
    }

    public boolean canResultsBePublic(Integer courseExecutionId) {
        return isCompleted() &&
                getQuiz().getCourseExecution().getId().equals(courseExecutionId) &&
                ((!getQuiz().getType().equals(Quiz.QuizType.IN_CLASS) && !getQuiz().getType().equals(Quiz.QuizType.TOURNAMENT))
                        || getQuiz().getResultsDate().isBefore(DateHandler.now()));
    }

    public void calculateStatistics() {
        if (!this.usedInStatistics) {
            user.increaseNumberOfQuizzes(getQuiz().getType());

            getQuestionAnswers().forEach(questionAnswer -> {
                user.increaseNumberOfAnswers(getQuiz().getType());
                if (questionAnswer.isCorrect()) {
                    user.increaseNumberOfCorrectAnswers(getQuiz().getType());
                }
            });

            getQuestionAnswers().forEach(questionAnswer ->
                    questionAnswer.getQuizQuestion().getQuestion().addAnswerStatistics(questionAnswer));

            this.usedInStatistics = true;
        }
    }

    public void remove() {
        user.getQuizAnswers().remove(this);
        user = null;

        quiz.getQuizAnswers().remove(this);
        quiz = null;

        questionAnswers.forEach(QuestionAnswer::remove);
    }

    public boolean openToAnswer() {
        return !isCompleted() && !(getQuiz().isOneWay() && getCreationDate() != null);
    }

    public long getNumberOfAnsweredQuestions() {
        return getQuestionAnswers().stream().filter(QuestionAnswer::isAnswered).count();
    }

    public long getNumberOfCorrectAnswers() {
        return getQuestionAnswers().stream().filter(QuestionAnswer::isCorrect).count();
    }
}
