package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard;

import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

public class DashboardDto {
    private Integer id;
    private String lastCheckFailedAnswers;
    private List<FailedAnswerDto> failedAnswers = new ArrayList<>();
    private String lastCheckDifficultQuestions;
    private String currentWeek;

    public DashboardDto(Dashboard dashboard) {
        id = dashboard.getId();
        lastCheckFailedAnswers = DateHandler.toISOString(dashboard.getLastCheckFailedAnswers());
        lastCheckDifficultQuestions = DateHandler.toISOString(dashboard.getLastCheckDifficultQuestions());
        currentWeek = DateHandler.toISOString(dashboard.getCurrentWeek());
        this.failedAnswers = dashboard.getFailedAnswers().stream()
                .map(failedAnswer -> {
                    return new FailedAnswerDto(failedAnswer);
                })
                .collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastCheckFailedAnswers() {
        return lastCheckFailedAnswers;
    }

    public void setLastCheckFailedAnswers(String lastCheckFailedAnswers) {
        this.lastCheckFailedAnswers = lastCheckFailedAnswers;
    }

    public String getLastCheckDifficultQuestions() {
        return lastCheckDifficultQuestions;
    }

    public void setLastCheckDifficultQuestions(String lastCheckDifficultQuestions) {
        this.lastCheckDifficultQuestions = lastCheckDifficultQuestions;
    }

    public String getCurrentWeek() {
        return currentWeek;
    }

    public void setCurrentWeek(String currentWeek) {
        this.currentWeek = currentWeek;
    }

    public List<FailedAnswerDto> getFailedAnswers() {
        return failedAnswers;
    }

    public void setFailedAnswers(List<FailedAnswerDto> failedAnswers) {
        this.failedAnswers = failedAnswers;
    }

    @Override
    public String toString() {
        return "DashboardDto{" +
                "id=" + id +
                ", lastCheckFailedAnswers=" + lastCheckFailedAnswers +
                ", lastCheckDifficultAnswers=" + lastCheckDifficultQuestions +
                ", currentWeek=" + currentWeek +
                ", failedAnswers=" + failedAnswers +
                "}";
    }
}
