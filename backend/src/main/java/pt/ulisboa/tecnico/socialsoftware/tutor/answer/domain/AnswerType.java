package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerTypeDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDetailsDto;

import javax.persistence.*;

@Entity
@Table(name = "answer_type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_answer_type",
        columnDefinition = "varchar(32) not null default 'multiple_choice'",
        discriminatorType = DiscriminatorType.STRING)
public abstract class AnswerType implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "question_answer_id")
    private QuestionAnswer questionAnswer;

    public AnswerType() {

    }

    public AnswerType(QuestionAnswer questionAnswer) {
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

    public abstract AnswerTypeDto getAnswerTypeDto();

    public abstract StatementAnswerDetailsDto getStatementAnswerDetailsDto();
}
