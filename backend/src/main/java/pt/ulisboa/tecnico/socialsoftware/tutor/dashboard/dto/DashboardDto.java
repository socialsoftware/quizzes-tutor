package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

public class DashboardDto {
    private Integer id;

    private String lastCheckFailedAnswers;

    private String lastCheckWeeklyScores;

    public DashboardDto() {
    }

    public DashboardDto(Dashboard dashboard) {
        id = dashboard.getId();
        lastCheckFailedAnswers = DateHandler.toISOString(dashboard.getLastCheckFailedAnswers());
        lastCheckWeeklyScores = DateHandler.toISOString(dashboard.getLastCheckWeeklyScores());
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

    public String getLastCheckWeeklyScores() {
        return lastCheckWeeklyScores;
    }

    public void setLastCheckWeeklyScores(String lastCheckWeeklyScores) {
        this.lastCheckWeeklyScores = lastCheckWeeklyScores;
    }

    @Override
    public String toString() {
        return "DashboardDto{" +
                "id=" + id +
                ", lastCheckFailedAnswers=" + lastCheckFailedAnswers +
                ", lastWeeklyStats=" + lastCheckWeeklyScores +
                "}";
    }
}
