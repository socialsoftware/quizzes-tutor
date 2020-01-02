package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import java.io.Serializable;

public class CourseDto implements Serializable {
    private String name;
    private String acronym;
    private String academicTerm;

    public CourseDto(CourseExecution courseExecution) {
        this.name = courseExecution.getCourse().getName();
        this.acronym = courseExecution.getAcronym();
        this.academicTerm = courseExecution.getAcademicTerm();
    }

    public CourseDto(String name, String acronym, String academicTerm) {
        this.name = name;
        this.acronym = acronym;
        this.academicTerm = academicTerm;
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
}
