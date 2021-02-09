package pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CourseExecutionDto implements Serializable {
    private Course.Type courseExecutionType;
    private Course.Type courseType;
    private CourseExecution.Status status;
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

    public CourseExecutionDto(Course course) {
        this.courseId = course.getId();
        this.courseType = course.getType();
        this.name = course.getName();
    }

    public CourseExecutionDto(CourseExecution courseExecution) {
        this.academicTerm = courseExecution.getAcademicTerm();
        this.acronym = courseExecution.getAcronym();
        this.courseExecutionId = courseExecution.getId();
        this.courseExecutionType = courseExecution.getType();
        this.courseId = courseExecution.getCourse().getId();
        this.courseType = courseExecution.getCourse().getType();
        this.name = courseExecution.getCourse().getName();
        this.status = courseExecution.getStatus();
        this.numberOfActiveTeachers = courseExecution.getNumberOfActiveTeachers();
        this.numberOfInactiveTeachers = courseExecution.getNumberofInactiveTeachers();
        this.numberOfActiveStudents = courseExecution.getNumberOfActiveStudents();
        this.numberOfInactiveStudents = courseExecution.getNumberOfInactiveStudents();
        this.numberOfQuizzes = courseExecution.getNumberOfQuizzes();
        this.numberOfQuestions = courseExecution.getNumberOfQuestions();
        if (courseExecution.getType().equals(Course.Type.EXTERNAL)) {
            this.courseExecutionUsers = new ArrayList<>();
            courseExecution.getUsers().stream().forEach(user -> {
                if (user.isStudent()) {
                    courseExecutionUsers.add(new StudentDto(user));
                } else {
                    courseExecutionUsers.add(new UserDto(user));
                }
            });
        }
    }

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

    public Course.Type getCourseType() {
        return courseType;
    }

    public void setCourseType(Course.Type courseType) {
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

    public Course.Type getCourseExecutionType() {
        return courseExecutionType;
    }

    public void setCourseExecutionType(Course.Type courseExecutionType) {
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

    public CourseExecution.Status getStatus() {
        return status;
    }

    public void setStatus(CourseExecution.Status status) {
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