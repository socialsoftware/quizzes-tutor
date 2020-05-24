package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fill_in_spot")
public class FillInSpot implements DomainEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private CodeFillInQuestion question;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fillInSpot",fetch = FetchType.EAGER, orphanRemoval=true)
    private final List<FillInOption> options = new ArrayList<>();

    public FillInSpot() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CodeFillInQuestion getQuestion() {
        return question;
    }

    public void setQuestion(CodeFillInQuestion question) {
        this.question = question;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitFillInSpot(this);
    }
}
