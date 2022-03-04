package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.FailedAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
public class FailedAnswer implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private boolean answered;

    private LocalDateTime collected;

    private boolean removed = false;

    @OneToOne
    private QuestionAnswer questionAnswer;

    @ManyToOne
    private Dashboard dashboard;

    public FailedAnswer(){
    }

    public FailedAnswer(Dashboard dashboard, QuestionAnswer questionAnswer){
        if (dashboard.getCourseExecution() != questionAnswer.getQuizAnswer().getQuiz().getCourseExecution()) {
            throw new TutorException(ErrorMessage.CANNOT_CREATE_FAILED_ANSWER);
        }

        setAnswered(questionAnswer.isAnswered());
        setQuestionAnswer(questionAnswer);
        setDashboard(dashboard);
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

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
        this.dashboard.addFailedAnswer(this);
    }

    @Override
    public void accept(Visitor visitor) {
        // TODO Auto-generated method stub
    }

    @Override
    public String toString() {
        return "FailedAnswer{" +
            "id=" + id +
            ", answered=" + answered +
            ", removed=" + removed +
            ", questionAnswer=" + questionAnswer +
            "}";
    }
}
