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
@Table(name = "fill_in_options")
public class FillInOption implements DomainEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column(nullable = false)
    private Integer sequence;

    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean correct;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "fill_in_id")
    private FillInSpot fillInSpot;

    public FillInOption() {}

    public FillInOption(OptionDto option) {
        setSequence(option.getSequence());
        setContent(option.getContent());
        setCorrect(option.getCorrect());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public FillInSpot getFillInSpot() {
        return fillInSpot;
    }

    public void setFillInSpot(FillInSpot fillInSpot) {
        this.fillInSpot = fillInSpot;
    }

    @Override
    public void accept(Visitor visitor) {
        throw new TutorException(ErrorMessage.ACCESS_DENIED);
    }
}
