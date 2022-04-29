package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import javax.persistence.*;

@Entity
public class DifficultQuestion implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int percentage;

    @ManyToOne
    private Question question;

    @ManyToOne
    private CourseExecution courseExecution;

    public DifficultQuestion() {
    }

    public DifficultQuestion(CourseExecution courseExecution, Question question, int percentage) {
        if (percentage < 0 || percentage > 24)
            throw new TutorException(ErrorMessage.CANNOT_CREATE_DIFFICULT_QUESTION);

        if (question.getCourse() != courseExecution.getCourse()) {
            throw new TutorException(ErrorMessage.CANNOT_CREATE_DIFFICULT_QUESTION);
        }

        setPercentage(percentage);
        setQuestion(question);
        setCourseExecution(courseExecution);
    }

    public void remove() {
        courseExecution.getDifficultQuestions().remove(this);
        courseExecution = null;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
        this.courseExecution.addDifficultQuestion(this);
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
        // Only used for XML generation
    }

    @Override
    public String toString() {
        return "DifficultQuestion{" +
                "id=" + id +
                ", percentage=" + percentage +
                ", question=" + question +
                "}";
    }

}