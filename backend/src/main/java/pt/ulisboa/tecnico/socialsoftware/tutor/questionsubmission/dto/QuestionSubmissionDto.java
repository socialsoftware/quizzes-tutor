package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;

import java.io.Serializable;

public class QuestionSubmissionDto implements Serializable {
    private Integer id;
    private Integer courseExecutionId;
    private QuestionDto question;
    private Integer userId;
    private String name;

    public QuestionSubmissionDto(){}

    public QuestionSubmissionDto(QuestionSubmission questionSubmission){
        setId(questionSubmission.getId());
        setCourseExecutionId(questionSubmission.getCourseExecution().getId());
        if (questionSubmission.getQuestion() != null)
            setQuestion(new QuestionDto(questionSubmission.getQuestion()));
        setUserId(questionSubmission.getUser().getId());
        setName(questionSubmission.getUser().getName());
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getCourseExecutionId() { return courseExecutionId; }

    public void setCourseExecutionId(Integer courseExecutionId) { this.courseExecutionId = courseExecutionId; }

    public QuestionDto getQuestion() { return question; }

    public void setQuestion(QuestionDto question) { this.question = question; }

    public Integer getUserId() { return userId; }

    public void setUserId(Integer userId) { this.userId = userId; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "QuestionDto{" +
                "id=" + id +
                ", courseExecutionId=" + courseExecutionId +
                ", userId=" + userId +
                ", name=" + name +
                ", questionDto=" + question +
                '}';
    }
}
