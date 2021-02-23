package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto;

import javax.persistence.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_SEQUENCE_FOR_QUESTION_ANSWER;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_ANSWER_HAS_DISCUSSION;

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

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "quiz_question_id")
    private QuizQuestion quizQuestion;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "quiz_answer_id")
    private QuizAnswer quizAnswer;

    private Integer sequence;

    @OneToOne(mappedBy = "questionAnswer", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private AnswerDetails answerDetails;

    @OneToOne(mappedBy = "questionAnswer")
    private Discussion discussion;

    public QuestionAnswer() {
    }

    public QuestionAnswer(QuizAnswer quizAnswer, QuizQuestion quizQuestion, Integer timeTaken, int sequence) {
        setTimeTaken(timeTaken);
        setQuizAnswer(quizAnswer);
        setQuizQuestion(quizQuestion);
        setSequence(sequence);
    }

    public QuestionAnswer(QuizAnswer quizAnswer, QuizQuestion quizQuestion, int sequence) {
        setQuizAnswer(quizAnswer);
        setQuizQuestion(quizQuestion);
        setSequence(sequence);
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

    public Question getQuestion() {
        return quizQuestion.getQuestion();
    }

    public QuizAnswer getQuizAnswer() {
        return quizAnswer;
    }

    public void setQuizAnswer(QuizAnswer quizAnswer) {
        this.quizAnswer = quizAnswer;
        quizAnswer.addQuestionAnswer(this);
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        if (sequence == null || sequence < 0)
            throw new TutorException(INVALID_SEQUENCE_FOR_QUESTION_ANSWER);

        this.sequence = sequence;
    }

    public AnswerDetails getAnswerDetails() {
        return answerDetails;
    }

    public void setAnswerDetails(AnswerDetails answerDetails) {
        this.answerDetails = answerDetails;
        if (this.answerDetails != null) {
            this.answerDetails.setQuestionAnswer(this);
        }
    }

    public AnswerDetails setAnswerDetails(StatementAnswerDto statementAnswerDto) {
        this.answerDetails = statementAnswerDto.getAnswerDetails(this);
        if (this.answerDetails != null) {
            this.answerDetails.setQuestionAnswer(this);
        }
        return this.answerDetails;
    }

    public boolean isCorrect() {
        return getAnswerDetails() != null && getAnswerDetails().isCorrect();
    }

    public void remove() {
        if (discussion != null) {
            throw new TutorException(QUESTION_ANSWER_HAS_DISCUSSION);
        }

        quizQuestion.getQuestionAnswers().remove(this);
        quizQuestion = null;

        if (answerDetails != null) {
            answerDetails.remove();
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionAnswer(this);
    }

    public StatementAnswerDetailsDto getStatementAnswerDetailsDto() {
        if (this.getAnswerDetails() == null) {
            return this.getQuestion().getEmptyStatementAnswerDetailsDto();
        } else {
            return this.getAnswerDetails().getStatementAnswerDetailsDto();
        }
    }

    public Discussion getDiscussion() {
        return discussion;
    }

    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }

    public boolean isAnswered() {
        return this.getTimeTaken() != null && this.getAnswerDetails() != null && this.getAnswerDetails().isAnswered();
    }
}