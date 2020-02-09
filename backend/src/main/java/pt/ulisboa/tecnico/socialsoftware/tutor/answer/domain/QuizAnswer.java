package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.Importable;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "quiz_answers")
public class QuizAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "answer_date")
    private LocalDateTime answerDate;

    private boolean completed;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quizAnswer", fetch=FetchType.LAZY, orphanRemoval=true)
    private Set<QuestionAnswer> questionAnswers = new HashSet<>();

    public QuizAnswer() {
    }

    public QuizAnswer(User user, Quiz quiz) {
        this.completed = false;
        this.user = user;
        user.addQuizAnswer(this);
        this.quiz = quiz;
        quiz.addQuizAnswer(this);

        quiz.getQuizQuestions().forEach(quizQuestion -> questionAnswers.add(new QuestionAnswer(this, quizQuestion, quizQuestion.getSequence())));
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
    
    public LocalDateTime getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = answerDate;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
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

    public void addQuestionAnswer(QuestionAnswer questionAnswer) {
        if (questionAnswers == null) {
            questionAnswers = new HashSet<>();
        }
        questionAnswers.add(questionAnswer);
    }

}
