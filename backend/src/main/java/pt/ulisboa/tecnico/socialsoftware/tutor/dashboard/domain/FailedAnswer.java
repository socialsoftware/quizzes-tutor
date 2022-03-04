package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.FailedAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
public class FailedAnswer implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime collected;

    private boolean answered;

    @OneToOne
    private QuestionAnswer questionAnswer;

    @ManyToOne
    private Dashboard dashboard;

    public FailedAnswer(){
    }

    public FailedAnswer(Dashboard dashboard, QuestionAnswer questionAnswer, LocalDateTime collected){
        if (dashboard.getCourseExecution() != questionAnswer.getQuizAnswer().getQuiz().getCourseExecution()) {
            throw new TutorException(ErrorMessage.CANNOT_CREATE_FAILED_ANSWER);
        }

        if (dashboard.getStudent() != questionAnswer.getQuizAnswer().getStudent()) {
            throw new TutorException(ErrorMessage.CANNOT_CREATE_FAILED_ANSWER);
        }

        if (!questionAnswer.getQuizAnswer().isCompleted() || questionAnswer.isCorrect()) {
            throw new TutorException(ErrorMessage.CANNOT_CREATE_FAILED_ANSWER);
        }

        setCollected(collected);
        setAnswered(questionAnswer.isAnswered());
        setQuestionAnswer(questionAnswer);
        setDashboard(dashboard);
    }

    public void remove() {
        if (collected.isAfter(DateHandler.now().minusDays(5))) {
            throw new TutorException(ErrorMessage.CANNOT_REMOVE_FAILED_ANSWER);
        }

        dashboard.getFailedAnswers().remove(this);
        dashboard = null;
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getCollected() {
        return collected;
    }

    public void setCollected(LocalDateTime collected) {
        this.collected = collected;
    }

    public boolean getAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
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
            ", questionAnswer=" + questionAnswer +
            "}";
    }

}
