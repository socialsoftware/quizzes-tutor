package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UpdatedFailedAnswersDto {
    private String lastCheckFailedAnswers;
    private List<FailedAnswerDto> failedAnswers;

    public UpdatedFailedAnswersDto(Dashboard dashboard) {
        this.lastCheckFailedAnswers = DateHandler.toISOString(dashboard.getLastCheckFailedAnswers());
        this.failedAnswers = dashboard.getFailedAnswers().stream()
                .map(FailedAnswerDto::new)
                .sorted(Comparator.comparing(FailedAnswerDto::getCollected, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public String getLastCheckFailedAnswers() {
        return lastCheckFailedAnswers;
    }

    public void setLastCheckFailedAnswers(String lastCheckFailedAnswers) {
        this.lastCheckFailedAnswers = lastCheckFailedAnswers;
    }

    public List<FailedAnswerDto> getFailedAnswers() {
        return failedAnswers;
    }

    public void setFailedAnswers(List<FailedAnswerDto> failedAnswers) {
        this.failedAnswers = failedAnswers;
    }

    @Override
    public String toString() {
        return "UpdatedFailedAnswersDto{" +
                "lastCheckFailedAnswers='" + lastCheckFailedAnswers + '\'' +
                ", failedAnswers=" + failedAnswers +
                '}';
    }
}
