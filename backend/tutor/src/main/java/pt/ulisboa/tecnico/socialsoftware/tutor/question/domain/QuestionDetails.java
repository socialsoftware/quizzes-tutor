package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuestionDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.QuestionDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "question_details")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_type",
        columnDefinition = "varchar(32) not null default 'multiple_choice'",
        discriminatorType = DiscriminatorType.STRING)
public abstract class QuestionDetails implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Question question;

    protected QuestionDetails(Question question) {
        this.question = question;
    }

    protected QuestionDetails() {

    }

    public Integer getId() {
        return id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public abstract CorrectAnswerDetailsDto getCorrectAnswerDetailsDto();

    public abstract StatementQuestionDetailsDto getStatementQuestionDetailsDto();

    public abstract StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto();

    public abstract AnswerDetailsDto getEmptyAnswerDetailsDto();

    public abstract QuestionDetailsDto getQuestionDetailsDto();

    public void delete() {
        this.question.setQuestionDetails(null);
        this.question = null;
    }

    public abstract void update(QuestionDetailsDto questionDetailsDto);

    public abstract String getCorrectAnswerRepresentation();

    public abstract String getAnswerRepresentation(List<Integer> selectedIds);
}
