package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerTypeDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerTypeDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionTypeDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuestionDetailsDto;

import javax.persistence.*;

@Entity
@Table(name = "question_type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_type",
        columnDefinition = "varchar(32) not null default 'multiple_choice'",
        discriminatorType = DiscriminatorType.STRING)
public abstract class QuestionType implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval=true)
    private Question question;

    public QuestionType(Question question) {
        this.question = question;
    }

    public QuestionType() {

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

    public abstract Integer getCorrectAnswer();

    public abstract CorrectAnswerTypeDto getCorrectAnswerDto();

    public abstract StatementQuestionDetailsDto getStatementQuestionDetailsDto();

    public abstract AnswerTypeDto getEmptyAnswerTypeDto();

    public abstract QuestionTypeDto getQuestionTypeDto();

    public void delete(){
        this.question.setQuestion(null);
        this.question = null;
    }

    public abstract void update(Updator updator);
}
