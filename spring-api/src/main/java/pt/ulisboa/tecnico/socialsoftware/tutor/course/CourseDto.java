package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import java.io.Serializable;

public class CourseDto implements Serializable {
    private int id;
    private String name;
    private String acronym;
    private String academicTerm;
    private String status;

    public CourseDto() {
    }

    public CourseDto(Course course) {
        this.name = course.getName();
    }

    public CourseDto(CourseExecution courseExecution) {
        this.id = courseExecution.getId();
        this.name = courseExecution.getCourse().getName();
        this.acronym = courseExecution.getAcronym();
        this.academicTerm = courseExecution.getAcademicTerm();

    }

    public CourseDto(String name, String acronym, String academicTerm) {
        this.name = name;
        this.acronym = acronym;
        this.academicTerm = academicTerm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}