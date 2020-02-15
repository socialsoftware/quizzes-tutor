package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import java.io.Serializable;

public class CourseDto implements Serializable {
    private int courseId;
    private Course.Type courseType;
    private String name;
    private int courseExecutionId;
    private Course.Type courseExecutionType;
    private String acronym;
    private String academicTerm;
    private CourseExecution.Status status;

    public CourseDto() {}

    public CourseDto(Course course) {
        this.courseId = course.getId();
        this.courseType = course.getType();
        this.name = course.getName();
    }

    public CourseDto(CourseExecution courseExecution) {
        this.courseId = courseExecution.getCourse().getId();
        this.courseType = courseExecution.getCourse().getType();
        this.name = courseExecution.getCourse().getName();
        this.courseExecutionId = courseExecution.getId();
        this.courseExecutionType = courseExecution.getType();
        this.acronym = courseExecution.getAcronym();
        this.academicTerm = courseExecution.getAcademicTerm();
        this.status = courseExecution.getStatus();
    }

    public CourseDto(String name, String acronym, String academicTerm) {
        this.name = name;
        this.acronym = acronym;
        this.academicTerm = academicTerm;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getCourseExecutionId() {
        return courseExecutionId;
    }

    public void setCourseExecutionId(int courseExecutionId) {
        this.courseExecutionId = courseExecutionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getAcademicTerm() {
        return academicTerm;
    }

    public void setAcademicTerm(String academicTerm) {
        this.academicTerm = academicTerm;
    }

    public CourseExecution.Status getStatus() {
        return status;
    }

    public void setStatus(CourseExecution.Status status) {
        this.status = status;
    }

    public Course.Type getCourseType() {
        return courseType;
    }

    public void setCourseType(Course.Type courseType) {
        this.courseType = courseType;
    }

    public Course.Type getCourseExecutionType() {
        return courseExecutionType;
    }

    public void setCourseExecutionType(Course.Type courseExecutionType) {
        this.courseExecutionType = courseExecutionType;
    }
}