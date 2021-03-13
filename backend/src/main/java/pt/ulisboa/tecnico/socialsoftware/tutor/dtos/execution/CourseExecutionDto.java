package pt.ulisboa.tecnico.socialsoftware.tutor.dtos.execution;

import pt.ulisboa.tecnico.socialsoftware.tutor.dtos.course.CourseType;
import pt.ulisboa.tecnico.socialsoftware.tutor.dtos.user.UserDto;

import java.io.Serializable;
import java.util.List;

public class CourseExecutionDto implements Serializable {
    private CourseType courseExecutionType;
    private CourseType courseType;
    private CourseExecutionStatus status;
    private String endDate;
    private String academicTerm;
    private String acronym;
    private String name;
    private int courseExecutionId;
    private int courseId;
    private int numberOfQuestions;
    private int numberOfQuizzes;
    private int numberOfActiveStudents;
    private int numberOfInactiveStudents;
    private int numberOfActiveTeachers;
    private int numberOfInactiveTeachers;
    private List<UserDto> courseExecutionUsers;

    public CourseExecutionDto() {}

    public CourseExecutionDto(String name, String acronym, String academicTerm) {
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

    public CourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getCourseExecutionId() {
        return courseExecutionId;
    }

    public void setCourseExecutionId(int courseExecutionId) {
        this.courseExecutionId = courseExecutionId;
    }

    public int getNumberOfActiveStudents() {
        return numberOfActiveStudents;
    }

    public void setNumberOfActiveStudents(int numberOfActiveStudents) {
        this.numberOfActiveStudents = numberOfActiveStudents;
    }

    public int getNumberOfInactiveStudents() {
        return numberOfInactiveStudents;
    }

    public void setNumberOfInactiveStudents(int numberOfInactiveStudents) {
        this.numberOfInactiveStudents = numberOfInactiveStudents;
    }

    public int getNumberOfActiveTeachers() {
        return numberOfActiveTeachers;
    }

    public void setNumberOfActiveTeachers(int numberOfActiveTeachers) {
        this.numberOfActiveTeachers = numberOfActiveTeachers;
    }

    public int getNumberOfInactiveTeachers() {
        return numberOfInactiveTeachers;
    }

    public void setNumberOfInactiveTeachers(int numberOfInactiveTeachers) {
        this.numberOfInactiveTeachers = numberOfInactiveTeachers;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public int getNumberOfQuizzes() {
        return numberOfQuizzes;
    }

    public void setNumberOfQuizzes(int numberOfQuizzes) {
        this.numberOfQuizzes = numberOfQuizzes;
    }

    public CourseType getCourseExecutionType() {
        return courseExecutionType;
    }

    public void setCourseExecutionType(CourseType courseExecutionType) {
        this.courseExecutionType = courseExecutionType;
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

    public CourseExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(CourseExecutionStatus status) {
        this.status = status;
    }

    public List<UserDto> getCourseExecutionUsers() {
        return courseExecutionUsers;
    }

    public void setCourseExecutionUsers(List<UserDto> courseExecutionUsers) {
        this.courseExecutionUsers = courseExecutionUsers;
    }

    @Override
    public String toString() {
        return "CourseDto{" +
                "courseExecutionType=" + courseExecutionType +
                ", courseType=" + courseType +
                ", status=" + status +
                ", academicTerm='" + academicTerm + '\'' +
                ", acronym='" + acronym + '\'' +
                ", name='" + name + '\'' +
                ", courseExecutionId=" + courseExecutionId +
                ", courseId=" + courseId +
                ", numberOfQuestions=" + numberOfQuestions +
                ", numberOfQuizzes=" + numberOfQuizzes +
                ", numberOfActiveStudents=" + numberOfActiveStudents +
                ", numberOfInactiveStudents=" + numberOfInactiveStudents +
                ", numberOfActiveTeachers=" + numberOfActiveTeachers +
                ", numberofInactiveTeachers=" + numberOfInactiveTeachers +
                '}';
    }
}