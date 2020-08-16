package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserQuestionSubmissionInfoDto {
    private int userId;
    private int numQuestionSubmissions;
    private List<QuestionSubmissionDto> questionSubmissions = new ArrayList<>();
    private String username;
    private String name;

    public UserQuestionSubmissionInfoDto(User user, List<QuestionSubmissionDto> questionSubmissions) {
        setUserId(user.getId());
        setQuestionSubmissions(questionSubmissions);
        setUsername(user.getUsername());
        setName(user.getName());
    }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public int getNumQuestionSubmissions() { return numQuestionSubmissions; }

    public List<QuestionSubmissionDto> getQuestionSubmissions() { return questionSubmissions; }

    public void setQuestionSubmissions(List<QuestionSubmissionDto> questionSubmissions) {
        this.questionSubmissions = questionSubmissions;
        this.numQuestionSubmissions = questionSubmissions.size();
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public static Comparator<UserQuestionSubmissionInfoDto> NumSubmissionsComparator = (a, b) -> {
        if (a.getNumQuestionSubmissions() == b.getNumQuestionSubmissions()) {
            return a.getUserId() - b.getUserId();
        } else {
            return b.getNumQuestionSubmissions() - a.getNumQuestionSubmissions();
        }
    };
}
