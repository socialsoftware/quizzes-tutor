package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
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

    private LocalDateTime lastCheckDifficultQuestions;

    private LocalDateTime currentWeek;

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
        setLastCheckDifficultQuestions(currentDate);
        setCurrentWeek(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).atStartOfDay());
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

    public LocalDateTime getCurrentWeek() {
        return currentWeek;
    }

    public void setCurrentWeek(LocalDateTime currentWeek) {
        this.currentWeek = currentWeek;
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

    public List<FailedAnswer> getAllFailedAnswers(){
        return this.failedAnswers;
    }

    public List<FailedAnswer> getFailedAnswers(){
        return this.failedAnswers.stream().filter(fa -> !fa.getRemoved()).collect(Collectors.toList());
    }
    
    public void addFailedAnswer(FailedAnswer failedAnswer){
        failedAnswers.add(failedAnswer);
    }

    public void removeFailedAnswer(FailedAnswer fa){
        failedAnswers.remove(fa);
    }

    

    @Override
    public String toString() {
        return "Dashboard{" +
                "id=" + id +
                ", lastCheckFailedAnswers=" + lastCheckFailedAnswers +
                ", lastCheckDifficultAnswers=" + lastCheckDifficultQuestions +
                "}";
    }
}