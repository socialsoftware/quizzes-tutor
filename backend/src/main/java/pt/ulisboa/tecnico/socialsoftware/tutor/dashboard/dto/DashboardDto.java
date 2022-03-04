package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard;

import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

public class DashboardDto {
    private Integer id;
    private String lastCheckFailedAnswers;
    private String lastCheckDifficultQuestions;
    private String lastCheckWeeklyScores;

    public DashboardDto(Dashboard dashboard) {
        id = dashboard.getId();
        lastCheckFailedAnswers = DateHandler.toISOString(dashboard.getLastCheckFailedAnswers());
        lastCheckDifficultQuestions = DateHandler.toISOString(dashboard.getLastCheckDifficultQuestions());
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

    public String getLastCheckDifficultQuestions() {
        return lastCheckDifficultQuestions;
    }

    public void setLastCheckDifficultQuestions(String lastCheckDifficultQuestions) {
        this.lastCheckDifficultQuestions = lastCheckDifficultQuestions;
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
                ", lastCheckDifficultAnswers=" + lastCheckDifficultQuestions +
                ", lastWeeklyStats=" + lastCheckWeeklyScores +
                "}";
    }
}
