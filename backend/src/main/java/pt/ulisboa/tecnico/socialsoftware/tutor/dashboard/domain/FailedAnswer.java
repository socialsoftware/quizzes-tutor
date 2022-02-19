package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.time.LocalDateTime;

import javax.persistence.*;

//TODO ADD ANY REMAINING TAGS
@Entity
@Table(name = "failed_answer")
public class FailedAnswer implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "answered")
    private boolean answered;

    @Column(name = "collected")
    private LocalDateTime collected;

    @Column(name = "removed", columnDefinition = "boolean default false")
    private boolean removed = false;

    // TODO: Add orphan removal? Since removing the question answer should remove the failed answer (?)
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "quiz_answer_id")
    private QuestionAnswer questionAnswer;

    public FailedAnswer(boolean answered, QuestionAnswer questionAnswer){
        this.collected = DateHandler.now();
        this.questionAnswer = questionAnswer;
        this.answered = answered;
    }

    public Integer getId() {
        return id;
    }

    public boolean getAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public LocalDateTime getCollected() {
        return collected;
    }

    public void setCollected(LocalDateTime collected) {
        this.collected = collected;
    }

    public boolean getRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public QuestionAnswer getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(QuestionAnswer questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    @Override
    public void accept(Visitor visitor) {
        // TODO Auto-generated method stub
    }
}
