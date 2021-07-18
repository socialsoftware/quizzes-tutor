package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.*;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import javax.persistence.*;

import static pt.ulisboa.tecnico.socialsoftware.common.dtos.question.QuestionTypes.*;
import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.*;

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
        this.answerDetails = statementAnswerDto.getAnswerDetails() != null ? getAnswerDetailsType(statementAnswerDto.getAnswerDetails()) : null;
        if (this.answerDetails != null) {
            this.answerDetails.setQuestionAnswer(this);
        }
        return this.answerDetails;
    }

    public AnswerDetails getAnswerDetailsType(StatementAnswerDetailsDto statementAnswerDetailsDto) {
        switch (statementAnswerDetailsDto.getType()) {
            case MULTIPLE_CHOICE_QUESTION:
                MultipleChoiceAnswer multipleChoiceAnswer = new MultipleChoiceAnswer(this);
                multipleChoiceAnswer.setOption((MultipleChoiceQuestion) getQuestion().getQuestionDetails(), (MultipleChoiceStatementAnswerDetailsDto) statementAnswerDetailsDto);
                return  multipleChoiceAnswer;
            case CODE_FILL_IN_QUESTION:
                CodeFillInAnswer codeFillInAnswer = new CodeFillInAnswer(this);
                codeFillInAnswer.setFillInOptions((CodeFillInQuestion) getQuestion().getQuestionDetails(), (CodeFillInStatementAnswerDetailsDto) statementAnswerDetailsDto);
                return codeFillInAnswer;
            case CODE_ORDER_QUESTION:
                CodeOrderAnswer codeOrderAnswer = new CodeOrderAnswer(this);
                codeOrderAnswer.setOrderedSlots((CodeOrderQuestion) getQuestion().getQuestionDetails(), (CodeOrderStatementAnswerDetailsDto) statementAnswerDetailsDto);
                return codeOrderAnswer;
            default:
                throw new TutorException(INVALID_ANSWER_DETAILS, statementAnswerDetailsDto.toString());
        }
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

    public StatementQuestionDto getStatementQuestionDto(boolean ghost) {
        StatementQuestionDto dto = new StatementQuestionDto();
        Question question = getQuizQuestion().getQuestion();
        if (!ghost) {
            dto.setContent(question.getContent());
            if (question.getImage() != null) {
                dto.setImage(question.getImage().getDto());
            }

            dto.setQuestionDetails(question.getStatementQuestionDetailsDto());
        }

        dto.setSequence(getSequence());
        dto.setQuestionId(question.getId());
        return dto;
    }

    public StatementAnswerDto getStatementAnswerDto() {
        StatementAnswerDto dto = new StatementAnswerDto();
        dto.setTimeTaken(getTimeTaken());
        dto.setSequence(getSequence());
        dto.setQuestionAnswerId(getId());
        dto.setQuizQuestionId(getQuizQuestion().getId());

        dto.setAnswerDetails(getStatementAnswerDetailsDto());

        if (getDiscussion() != null) {
            dto.setUserDiscussion(getDiscussion().getDto(false));
        }
        return dto;
    }
}