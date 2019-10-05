package pt.ulisboa.tecnico.socialsoftware.tutor.courses.dto;

public class CourseExecutionDto {
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
}
