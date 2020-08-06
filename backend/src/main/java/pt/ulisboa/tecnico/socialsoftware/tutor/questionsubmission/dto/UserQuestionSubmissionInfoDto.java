package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

public class UserQuestionSubmissionInfoDto {
    private int userId;
    private int numQuestionSubmissions;
    private String username;
    private String name;

    public UserQuestionSubmissionInfoDto(User user, int numQuestionSubmissions) {
        setUserId(user.getId());
        setNumQuestionSubmissions(numQuestionSubmissions);
        setUsername(user.getUsername());
        setName(user.getName());
    }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public int getNumQuestionSubmissions() { return numQuestionSubmissions; }

    public void setNumQuestionSubmissions(int numQuestionSubmissions) { this.numQuestionSubmissions = numQuestionSubmissions; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public void addQuestionSubmission() { this.numQuestionSubmissions++;  }
}
