package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
public class Dashboard implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime lastCheckFailedAnswers;

    private LocalDateTime lastCheckDifficultQuestions;

    private LocalDateTime lastCheckWeeklyScores;

    @ManyToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private Student student;

    public Dashboard() {
    }

    public Dashboard(CourseExecution courseExecution, Student student) {
        LocalDateTime currentDate = DateHandler.now();
        setLastCheckFailedAnswers(currentDate);
        setLastCheckDifficultQuestions(currentDate);
        setLastCheckWeeklyScores(currentDate);
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

    public LocalDateTime getLastCheckDifficultQuestions() {
        return lastCheckDifficultQuestions;
    }

    public void setLastCheckDifficultQuestions(LocalDateTime lastCheckDifficultAnswers) {
        this.lastCheckDifficultQuestions = lastCheckDifficultAnswers;
    }

    public LocalDateTime getLastCheckWeeklyScores() {
        return lastCheckWeeklyScores;
    }

    public void setLastCheckWeeklyScores(LocalDateTime currentWeek) {
        this.lastCheckWeeklyScores = currentWeek;
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
        student.getDashboards().remove(this);
        student = null;
    }

    public void accept(Visitor visitor) {
    }

    @Override
    public String toString() {
        return "Dashboard{" +
                "id=" + id +
                ", lastCheckWeeklyScores=" + lastCheckWeeklyScores +
                ", lastCheckFailedAnswers=" + lastCheckFailedAnswers +
                ", lastCheckDifficultAnswers=" + lastCheckDifficultQuestions +
                "}";
    }
}