package pt.ulisboa.tecnico.socialsoftware.tutor.submission.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

public class UserSubmissionInfoDto {
    private int userId;
    private int numSubmissions;
    private String username;
    private String name;

    public UserSubmissionInfoDto(User user, int numSubmissions) {
        setUserId(user.getId());
        setNumSubmissions(numSubmissions);
        setUsername(user.getUsername());
        setName(user.getName());
    }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public int getNumSubmissions() { return numSubmissions; }

    public void setNumSubmissions(int numSubmissions) { this.numSubmissions = numSubmissions; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public void addSubmission() { this.numSubmissions++;  }
}
