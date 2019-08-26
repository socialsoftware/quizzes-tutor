package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "quiz_answers")
public class QuizAnswer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "assigned_date")
    private LocalDateTime assignedDate;

    @Column(name = "available_date")
    private LocalDateTime availableDate;

    @Column(name = "answer_date")
    private LocalDateTime answerDate;

    private Boolean completed;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quizAnswer")
    private Set<QuestionAnswer> questionAnswers;

    public QuizAnswer() {
    }

    public QuizAnswer(User user, Quiz quiz, LocalDateTime availableDate) {
        this.assignedDate = LocalDateTime.now();
        this.availableDate = availableDate;
        this.completed = false;
        this.user = user;
        user.addQuizAnswer(this);
        this.quiz = quiz;
        quiz.addQuizAnswer(this);
    }

    public void remove() {
        user.getQuizAnswers().remove(this);
        user = null;

        quiz.getQuizAnswers().remove(this);
        quiz = null;

        for (QuestionAnswer questionAnswer: getQuestionAnswers()) {
            questionAnswer.remove();
        }

        questionAnswers.clear();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDateTime assignedDate) {
        this.assignedDate = assignedDate;
    }

    public LocalDateTime getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(LocalDateTime availableDate) {
        this.availableDate = availableDate;
    }

    public LocalDateTime getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = answerDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<QuestionAnswer> getQuestionAnswers() {
        if (questionAnswers == null) {
            questionAnswers = new HashSet<>();
        }
        return questionAnswers;
    }

    public void setQuestionAnswers(Set<QuestionAnswer> questionAnswers) {
        this.questionAnswers = questionAnswers;
    }

    public void addQuestionAnswer(QuestionAnswer questionAnswer) {
        if (questionAnswers == null) {
            questionAnswers = new HashSet<>();
        }
        questionAnswers.add(questionAnswer);
    }

}
