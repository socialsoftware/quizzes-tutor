package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UserQuestionSubmissionInfoDto {
    private int submitterId;
    private int totalQuestionSubmissions = 0;
    private int numApprovedQuestionSubmissions = 0;
    private int numRejectedQuestionSubmissions = 0;
    private int numInReviewQuestionSubmissions = 0;
    private int numInRevisionQuestionSubmissions = 0;
    private List<QuestionSubmissionDto> questionSubmissions = new ArrayList<>();
    private String username;
    private String name;

    public UserQuestionSubmissionInfoDto(User user) {
        setUserId(user.getId());
        setQuestionSubmissions(user.getQuestionSubmissions().stream().map(QuestionSubmissionDto::new).collect(Collectors.toList()));
        setNumQuestionSubmissions();
        setUsername(user.getUsername());
        setName(user.getName());
    }

    public int getSubmitterId() { return submitterId; }

    public void setUserId(int submitterId) { this.submitterId = submitterId; }

    public int getTotalQuestionSubmissions() { return totalQuestionSubmissions; }

    public List<QuestionSubmissionDto> getQuestionSubmissions() { return questionSubmissions; }

    public void setQuestionSubmissions(List<QuestionSubmissionDto> questionSubmissions) {
        this.questionSubmissions = questionSubmissions;
    }

    public void setNumQuestionSubmissions() {
        this.totalQuestionSubmissions = this.questionSubmissions.size();

        for (QuestionSubmissionDto questionSubmissionDto: this.questionSubmissions) {
            if (questionSubmissionDto.getStatus().equals(QuestionSubmission.Status.APPROVED.name())) {
                numApprovedQuestionSubmissions++;
            } else if (questionSubmissionDto.getStatus().equals(QuestionSubmission.Status.REJECTED.name())) {
                numRejectedQuestionSubmissions++;
            } else if (questionSubmissionDto.getStatus().equals(QuestionSubmission.Status.IN_REVIEW.name())) {
                numInReviewQuestionSubmissions++;
            } else if (questionSubmissionDto.getStatus().equals(QuestionSubmission.Status.IN_REVISION.name())) {
                numInRevisionQuestionSubmissions++;
            }
        }
    }

    public int getNumApprovedQuestionSubmissions() { return numApprovedQuestionSubmissions; }

    public int getNumRejectedQuestionSubmissions() { return numRejectedQuestionSubmissions; }

    public int getNumInReviewQuestionSubmissions() { return numInReviewQuestionSubmissions; }

    public int getNumInRevisionQuestionSubmissions() { return numInRevisionQuestionSubmissions; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public static final Comparator<UserQuestionSubmissionInfoDto> numSubmissionsComparator = (a, b) -> {
        if (a.getTotalQuestionSubmissions() == b.getTotalQuestionSubmissions()) {
            return a.getSubmitterId() - b.getSubmitterId();
        } else {
            return b.getTotalQuestionSubmissions() - a.getTotalQuestionSubmissions();
        }
    };
}
