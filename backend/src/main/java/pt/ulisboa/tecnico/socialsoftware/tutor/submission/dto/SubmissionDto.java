package pt.ulisboa.tecnico.socialsoftware.tutor.submission.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain.Submission;

import java.io.Serializable;

public class SubmissionDto implements Serializable {
    private Integer id;
    private Integer courseExecutionId;
    private QuestionDto question;
    private Integer userId;
    private String name;
    private String argument;
    private boolean anonymous;

    public SubmissionDto(){}

    public SubmissionDto(Submission submission){
        setId(submission.getId());
        setCourseExecutionId(submission.getCourseExecution().getId());
        if(submission.getQuestion() != null)
            setQuestion(new QuestionDto(submission.getQuestion()));
        setUserId(submission.getUser().getId());
        setName(submission.getUser().getName());
        setAnonymous(submission.isAnonymous());
        setArgument(submission.getArgument());
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

    public boolean isAnonymous() { return anonymous; }

    public void setAnonymous(boolean anonymous) { this.anonymous = anonymous; }

    public String getArgument() { return argument; }

    public void setArgument(String argument) { this.argument = argument; }
}
