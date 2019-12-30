package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import java.io.Serializable;

public class CourseDto implements Serializable {
    private String name;
    private String acronym;

    public CourseDto(CourseExecution course) {
        this.name = course.getName();
        this.acronym = course.getAcronym();
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

}
