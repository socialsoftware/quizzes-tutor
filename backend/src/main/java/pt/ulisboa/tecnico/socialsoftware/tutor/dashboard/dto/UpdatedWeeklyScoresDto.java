package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DifficultQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.WeeklyScore;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UpdatedWeeklyScoresDto {
    private String lastCheckWeeklyScores;
    private List<WeeklyScoreDto> weeklyScores;

    public UpdatedWeeklyScoresDto(Dashboard dashboard) {
        this.lastCheckWeeklyScores = DateHandler.toISOString(dashboard.getLastCheckWeeklyScores());
        this.weeklyScores = dashboard.getWeeklyScores().stream()
                .sorted(Comparator.comparing(WeeklyScore::getWeek, Comparator.reverseOrder()))
                .map(WeeklyScoreDto::new)
                .collect(Collectors.toList());
    }

    public String getLastCheckWeeklyScores() {
        return lastCheckWeeklyScores;
    }

    public void setLastCheckWeeklyScores(String lastCheckWeeklyScores) {
        this.lastCheckWeeklyScores = lastCheckWeeklyScores;
    }

    public List<WeeklyScoreDto> getWeeklyScores() {
        return weeklyScores;
    }

    public void setWeeklyScores(List<WeeklyScoreDto> weeklyScores) {
        this.weeklyScores = weeklyScores;
    }

    @Override
    public String toString() {
        return "UpdatedWeeklyScoresDto{" +
                "lastCheckWeeklyScores='" + lastCheckWeeklyScores + '\'' +
                ", weeklyScores=" + weeklyScores +
                '}';
    }
}
