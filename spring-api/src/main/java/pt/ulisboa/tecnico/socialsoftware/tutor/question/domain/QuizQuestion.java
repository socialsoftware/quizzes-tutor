package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Answer;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="quiz_questions")
public class QuizQuestion {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quizQuestion")
    private Set<Answer> answers;

    private Integer sequence;

    public QuizQuestion(){

    }

    public QuizQuestion(Quiz quiz, Question question, Integer sequence) {
        this.quiz = quiz;
        this.question = question;
        this.sequence = sequence;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}