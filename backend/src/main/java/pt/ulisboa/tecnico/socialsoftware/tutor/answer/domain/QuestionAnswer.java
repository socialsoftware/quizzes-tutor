package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import javax.persistence.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_SEQUENCE_FOR_QUESTION_ANSWER;

@Entity
@Table(name = "question_answers",
        indexes = {
            @Index(name = "question_answers_indx_0", columnList = "quiz_question_id")
        }
)

public class QuestionAnswer implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "time_taken")
    private Integer timeTaken;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name = "quiz_question_id")
    private QuizQuestion quizQuestion;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name = "quiz_answer_id")
    private QuizAnswer quizAnswer;

    @ManyToOne(fetch=FetchType.EAGER, optional=true)
    @JoinColumn(name = "option_id")
    private Option option;

    private Integer sequence;

    @OneToOne(mappedBy = "questionAnswer")
    private Discussion discussion;

    public QuestionAnswer() {
    }

    public QuestionAnswer(QuizAnswer quizAnswer, QuizQuestion quizQuestion, Integer timeTaken, Option option, int sequence) {
        setTimeTaken(timeTaken);
        setQuizAnswer(quizAnswer);
        setQuizQuestion(quizQuestion);
        setOption(option);
        setSequence(sequence);
    }

    public QuestionAnswer(QuizAnswer quizAnswer, QuizQuestion quizQuestion, int sequence) {
        setQuizAnswer(quizAnswer);
        setQuizQuestion(quizQuestion);
        setSequence(sequence);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionAnswer(this);
    }

    public Integer getId() {
        return id;
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
        quizQuestion.addQuestionAnswer(this);
    }

    public QuizAnswer getQuizAnswer() {
        return quizAnswer;
    }

    public void setQuizAnswer(QuizAnswer quizAnswer) {
        this.quizAnswer = quizAnswer;
        quizAnswer.addQuestionAnswer(this);
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;

        if (option != null)
            option.addQuestionAnswer(this);
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        if (sequence == null || sequence < 0)
            throw new TutorException(INVALID_SEQUENCE_FOR_QUESTION_ANSWER);

        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "QuestionAnswer{" +
                "id=" + id +
                ", timeTaken=" + timeTaken +
                ", quizQuestion=" + quizQuestion.getId() +
                ", quizAnswer=" + quizAnswer.getId() +
                ", option=" + option.getId() +
                ", sequence=" + sequence +
                '}';
    }

    public boolean isCorrect() {
        return getOption() != null && getOption().getCorrect();
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

        if (discussion != null) {
            discussion.setQuestionAnswer(null);
            discussion.remove();
            discussion = null;
        }
    }

    public Discussion getDiscussion() {
        return discussion;
    }

    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }
}