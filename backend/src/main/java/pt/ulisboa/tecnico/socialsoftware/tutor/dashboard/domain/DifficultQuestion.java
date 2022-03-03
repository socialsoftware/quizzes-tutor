package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
public class DifficultQuestion implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int percentage;

    private LocalDateTime collected;

    private boolean removed = false;

    @ManyToOne
    private Question question;

    @ManyToOne
    private Dashboard dashboard;

    public DifficultQuestion(){
    }

    public DifficultQuestion(Dashboard dashboard, Question question, int percentage, LocalDateTime collected){
        setPercentage(percentage);
        setCollected(collected);
        setRemoved(false);
        setQuestion(question);
        setDashboard(dashboard);
    }

    public void remove() {
        if (!removed) {
            throw new TutorException(ErrorMessage.CANNOT_REMOVE_DIFFICULT_QUESTION);
        } else if (collected.isBefore(DateHandler.now().minusDays(7))) {
            throw new TutorException(ErrorMessage.CANNOT_REMOVE_DIFFICULT_QUESTION);
        }

        dashboard.getDifficultQuestions().remove(this);
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public Integer getId() {
        return id;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public LocalDateTime getCollected() {
        return collected;
    }

    public void setCollected(LocalDateTime collected) {
        this.collected = collected;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void update() {
        this.setPercentage(this.getQuestion().getDifficulty());
    }

    @Override
    public void accept(Visitor visitor) {
        // TODO Auto-generated method stub
    }

    @Override
    public String toString() {
        return "DifficultQuestion{" +
                "id=" + id +
                ", percentage=" + percentage +
                ", removed=" + removed +
                ", question=" + question +
                "}";
    }

}