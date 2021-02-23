package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_QUESTION_HAS_ANSWERS;

@Entity
@Table(name="quiz_questions")
public class QuizQuestion implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany(mappedBy = "quizQuestion", fetch=FetchType.LAZY)
    private Set<QuestionAnswer> questionAnswers = new HashSet<>();

    private Integer sequence;

    public QuizQuestion() {
    }

    public QuizQuestion(Quiz quiz, Question question, Integer sequence) {
        setQuiz(quiz);
        setQuestion(question);
        setSequence(sequence);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuizQuestion(this);
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
        quiz.addQuizQuestion(this);
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
        question.addQuizQuestion(this);
    }

    public Integer getSequence() {
        return sequence;
    }

    public Integer getId() {
        return id;
    }

    public Set<QuestionAnswer> getQuestionAnswers() {
        return questionAnswers;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public void addQuestionAnswer(QuestionAnswer questionAnswer) {
        questionAnswers.add(questionAnswer);
    }

    @Override
    public String toString() {
        return "QuizQuestion{" +
                "id=" + id +
                ", sequence=" + sequence +
                '}';
    }

    public void remove() {
        this.quiz.removeQuizQuestion(this);
        quiz = null;
        this.question.getQuizQuestions().remove(this);
        question = null;
    }

    void checkCanRemove() {
        if (!questionAnswers.isEmpty()) {
            throw new TutorException(QUIZ_QUESTION_HAS_ANSWERS);
        }
    }
}