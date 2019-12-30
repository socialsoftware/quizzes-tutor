package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import java.io.Serializable;

public class CourseDto implements Serializable {
    private Integer name;
    private Integer acronym;
    private Integer year;

    public CourseDto(Course course) {
        this.name = course.getName();
        this.acronym = course.getAcronym();
        this.year = year;
    }

    public Integer getName() {
        return name;
    }

    public void setName(Integer name) {
        this.name = name;
    }

    public Integer getAcronym() {
        return acronym;
    }

    public void setAcronym(Integer acronym) {
        this.acronym = acronym;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
