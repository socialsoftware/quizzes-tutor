package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserQuestionSubmissionInfoDto {
    private int userId;
    private int totalQuestionSubmissions = 0;
    private int numAvailableQuestionSubmissions = 0;
    private int numDisabledQuestionSubmissions = 0;
    private int numRejectedQuestionSubmissions = 0;
    private int numInReviewQuestionSubmissions = 0;
    private int numInRevisionQuestionSubmissions = 0;
    private List<QuestionSubmissionDto> questionSubmissions = new ArrayList<>();
    private String username;
    private String name;

    public UserQuestionSubmissionInfoDto(User user, List<QuestionSubmissionDto> questionSubmissions) {
        setUserId(user.getId());
        setQuestionSubmissions(questionSubmissions);
        setNumQuestionSubmissions();
        setUsername(user.getUsername());
        setName(user.getName());
    }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public int getTotalQuestionSubmissions() { return totalQuestionSubmissions; }

    public List<QuestionSubmissionDto> getQuestionSubmissions() { return questionSubmissions; }

    public void setQuestionSubmissions(List<QuestionSubmissionDto> questionSubmissions) {
        this.questionSubmissions = questionSubmissions;
    }

    public void setNumQuestionSubmissions() {
        this.totalQuestionSubmissions = this.questionSubmissions.size();

        for (QuestionSubmissionDto questionSubmissionDto: this.questionSubmissions) {
            if (questionSubmissionDto.getQuestion().getStatus().equals(Question.Status.AVAILABLE.name())) {
                numAvailableQuestionSubmissions++;
            } else if (questionSubmissionDto.getQuestion().getStatus().equals(Question.Status.DISABLED.name())) {
                numDisabledQuestionSubmissions++;
            } else if (questionSubmissionDto.getQuestion().getStatus().equals(Question.Status.REJECTED.name())) {
                numRejectedQuestionSubmissions++;
            } else if (questionSubmissionDto.getQuestion().getStatus().equals(Question.Status.IN_REVIEW.name())) {
                numInReviewQuestionSubmissions++;
            } else if (questionSubmissionDto.getQuestion().getStatus().equals(Question.Status.IN_REVISION.name())) {
                numInRevisionQuestionSubmissions++;
            }
        }
    }

    public int getNumAvailableQuestionSubmissions() { return numAvailableQuestionSubmissions; }

    public int getNumDisabledQuestionSubmissions() { return numDisabledQuestionSubmissions; }

    public int getNumRejectedQuestionSubmissions() { return numRejectedQuestionSubmissions; }

    public int getNumInReviewQuestionSubmissions() { return numInReviewQuestionSubmissions; }

    public int getNumInRevisionQuestionSubmissions() { return numInRevisionQuestionSubmissions; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public static Comparator<UserQuestionSubmissionInfoDto> NumSubmissionsComparator = (a, b) -> {
        if (a.getTotalQuestionSubmissions() == b.getTotalQuestionSubmissions()) {
            return a.getUserId() - b.getUserId();
        } else {
            return b.getTotalQuestionSubmissions() - a.getTotalQuestionSubmissions();
        }
    };
}
