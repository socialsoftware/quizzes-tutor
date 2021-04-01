package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDetailsDto;

import javax.persistence.*;

@Entity
@Table(name = "answer_details")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "answer_details_type",
        columnDefinition = "varchar(32) not null default 'multiple_choice'",
        discriminatorType = DiscriminatorType.STRING)
public abstract class AnswerDetails implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_answer_id")
    private QuestionAnswer questionAnswer;

    protected AnswerDetails() {

    }

    protected AnswerDetails(QuestionAnswer questionAnswer) {
        setQuestionAnswer(questionAnswer);
    }

    public Integer getId() {
        return id;
    }

    public QuestionAnswer getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(QuestionAnswer questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    public abstract boolean isCorrect();

    public abstract void remove();

    public abstract AnswerDetailsDto getAnswerDetailsDto();

    public abstract String getAnswerRepresentation();

    public abstract StatementAnswerDetailsDto getStatementAnswerDetailsDto();

    public abstract boolean isAnswered();
}
