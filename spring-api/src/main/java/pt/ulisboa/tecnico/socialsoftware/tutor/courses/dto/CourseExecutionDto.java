package pt.ulisboa.tecnico.socialsoftware.tutor.courses.dto;

import java.io.Serializable;

public class CourseExecutionDto implements Serializable {
    private Integer year;

    public CourseExecutionDto(Integer year) {
        this.year = year;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "CourseExecutionDto{" +
                "year=" + year +
                '}';
    }
}
