package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import javax.persistence.*;

@Entity
@Table(name = "question_answers")
public class QuestionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "time_taken")
    private Integer timeTaken;

    @ManyToOne
    @JoinColumn(name = "quiz_question_id")
    private QuizQuestion quizQuestion;

    @ManyToOne
    @JoinColumn(name = "quiz_answer_id")
    private QuizAnswer quizAnswer;

    @ManyToOne
    @JoinColumn(name = "option_id")
    private Option option;

    private Integer sequence;

    public QuestionAnswer() {
    }

    public QuestionAnswer(QuizAnswer quizAnswer, QuizQuestion quizQuestion, Integer timeTaken, Option option, int sequence){
        this.timeTaken = timeTaken;
        this.quizAnswer = quizAnswer;
        quizAnswer.addQuestionAnswer(this);
        this.quizQuestion = quizQuestion;
        quizQuestion.addQuestionAnswer(this);
        this.option = option;
        if (option != null) {
            option.addQuestionAnswer(this);
        }
        this.sequence = sequence;
    }

    public QuestionAnswer(QuizAnswer quizAnswer, QuizQuestion quizQuestion, int sequence){
        this.quizAnswer = quizAnswer;
        quizAnswer.addQuestionAnswer(this);
        this.quizQuestion = quizQuestion;
        quizQuestion.addQuestionAnswer(this);
        this.sequence = sequence;
    }

    public void remove() {
        quizAnswer.getQuestionAnswers().remove(this);
        quizAnswer = null;

        quizQuestion.getQuestionAnswers().remove(this);
        quizQuestion = null;

        if (option != null) {
            option.getQuestionAnswers().remove(this);
            option = null;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Integer timeTaken) {
        this.timeTaken = timeTaken;
    }



    public QuizQuestion getQuizQuestion() {
        return quizQuestion;
    }

    public void setQuizQuestion(QuizQuestion quizQuestion) {
        this.quizQuestion = quizQuestion;
    }

    public QuizAnswer getQuizAnswer() {
        return quizAnswer;
    }

    public void setQuizAnswer(QuizAnswer quizAnswer) {
        this.quizAnswer = quizAnswer;
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "QuestionAnswer{" +
                "id=" + id +
                ", timeTaken=" + timeTaken +
                ", sequence=" + sequence +
                '}';
    }

    public boolean isCorrect() {
        return getOption() != null && getOption().getCorrect();
    }
}