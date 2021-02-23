package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeFillInAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "code_fill_in_options")
public class CodeFillInOption implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer sequence;

    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean correct;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_fill_in_id")
    private CodeFillInSpot codeFillInSpot;

    @ManyToMany()
    private final Set<CodeFillInAnswer> questionAnswers = new HashSet<>();

    public CodeFillInOption() {
    }

    public CodeFillInOption(OptionDto option) {
        setSequence(option.getSequence());
        setContent(option.getContent());
        setCorrect(option.isCorrect());
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

    public CodeFillInSpot getFillInSpot() {
        return codeFillInSpot;
    }

    public void setFillInSpot(CodeFillInSpot codeFillInSpot) {
        this.codeFillInSpot = codeFillInSpot;
    }

    public Set<CodeFillInAnswer> getQuestionAnswers() {
        return questionAnswers;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitFillInOption(this);
    }


    public void delete() {
        this.codeFillInSpot = null;
        this.questionAnswers.clear();
    }
}