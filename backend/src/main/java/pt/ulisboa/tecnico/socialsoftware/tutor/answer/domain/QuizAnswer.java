package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

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

    private Boolean fraud = false;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name = "user_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quizAnswer", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<QuestionAnswer> questionAnswers = new ArrayList<>();

    public QuizAnswer() {
    }

    public QuizAnswer(Student student, Quiz quiz) {
        setCompleted(false);
        setFraud(false);
        setStudent(student);
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

    public boolean isFraud() {
        return this.fraud != null ? this.fraud : false;
    }

    public void setFraud(boolean fraud) {
        this.fraud = fraud;
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

    @Override
    public String toString() {
        return "QuizAnswer{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", answerDate=" + answerDate +
                ", completed=" + completed +
                ", fraud=" + fraud +
                ", student=" + student +
                ", quiz=" + quiz +
                ", questionAnswers=" + questionAnswers +
                '}';
    }
}
