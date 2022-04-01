package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class SameQuestion implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private FailedAnswer failedAnswer;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "same_question_id")
    private Set<FailedAnswer> failedAnswers = new HashSet<>();

    public SameQuestion() {}

    public SameQuestion(FailedAnswer failedAnswer) {
        this.failedAnswer = failedAnswer;
    }

    public void remove() {
        failedAnswers.clear();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FailedAnswer getFailedAnswer() {
        return failedAnswer;
    }

    public void setFailedAnswer(FailedAnswer failedAnswer) {
        this.failedAnswer = failedAnswer;
    }

    public Set<FailedAnswer> getFailedAnswers() {
        return failedAnswers;
    }

    public void setFailedAnswers(Set<FailedAnswer> failedAnswers) {
        this.failedAnswers = failedAnswers;
    }

    @Override
    public void accept(Visitor visitor) {

    }
}
