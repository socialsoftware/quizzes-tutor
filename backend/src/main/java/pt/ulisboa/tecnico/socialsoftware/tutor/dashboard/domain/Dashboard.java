package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;

@Entity
public class Dashboard implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime lastCheckFailedAnswers;

    private LocalDateTime lastCheckDifficultAnswers;

    @ManyToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private Student student;

    @OneToMany
    private final List<FailedAnswer> failedAnswers = new ArrayList<>();

    public Dashboard() {
    }

    public Dashboard(CourseExecution courseExecution, Student student) {
        LocalDateTime currentDate = DateHandler.now();
        setLastCheckFailedAnswers(currentDate);
        setLastCheckDifficultAnswers(currentDate);
        setCourseExecution(courseExecution);
        setStudent(student);
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getLastCheckFailedAnswers() {
        return lastCheckFailedAnswers;
    }

    public void setLastCheckFailedAnswers(LocalDateTime lastCheckFailedAnswer) {
        this.lastCheckFailedAnswers = lastCheckFailedAnswer;
    }

    public LocalDateTime getLastCheckDifficultAnswers() {
        return lastCheckDifficultAnswers;
    }

    public void setLastCheckDifficultAnswers(LocalDateTime lastCheckDifficultAnswers) {
        this.lastCheckDifficultAnswers = lastCheckDifficultAnswers;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
        this.student.addDashboard(this);
    }

    public void remove() {
        if (student == null)
            return;

        student.getDashboards().remove(this);
    }

    public void accept(Visitor visitor) {
    }

    public void addFailedAnswer(FailedAnswer failedAnswer) {
        this.failedAnswers.add(failedAnswer);
    }

    public List<FailedAnswer> getFailedAnswers(){
        return this.failedAnswers.stream().filter(fa -> !fa.getRemoved()).collect(Collectors.toList());
    }

    public List<FailedAnswer> getAllFailedAnswers(){
        return this.failedAnswers;
    }

    @Override
    public String toString() {
        return "Dashboard{" +
                "id=" + id +
                ", lastCheckFailedAnswers=" + lastCheckFailedAnswers +
                ", lastCheckDifficultAnswers=" + lastCheckDifficultAnswers +
                "}";
    }
}