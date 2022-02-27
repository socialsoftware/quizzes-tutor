package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.WeeklyScore;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.WeeklyScoreDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.WeeklyScoreRepository;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class WeeklyScoreService {

  @Autowired
  private WeeklyScoreRepository weeklyScoreRepository;

  @Autowired
  private DashboardRepository dashboardRepository;

  @Retryable(
          value = {SQLException.class},
          backoff = @Backoff(delay = 5000))
  @Transactional(isolation = Isolation.READ_COMMITTED)
  public WeeklyScoreDto createWeeklyScore(Integer dashboardId) {
    if (dashboardId == null) {
      throw new TutorException(DASHBOARD_NOT_FOUND);
    }
    Dashboard dashboard = dashboardRepository.findById(dashboardId)
            .orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));

    TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY);
    LocalDate week = DateHandler.now().with(weekSunday).toLocalDate();

    Set<WeeklyScore> weeklyScores = dashboard.getWeeklyScores().stream()
            .filter(weeklyScore -> weeklyScore.getWeek().isEqual(week))
            .collect(Collectors.toSet());

    if (weeklyScores.size() > 0) {
      throw new TutorException(WEEKLY_SCORE_ALREADY_CREATED);
    }

    WeeklyScore weeklyScore = new WeeklyScore(dashboard);
    weeklyScoreRepository.save(weeklyScore);

    return new WeeklyScoreDto(weeklyScore);
  }

  @Retryable(
          value = {SQLException.class},
          backoff = @Backoff(delay = 5000))
  @Transactional(isolation = Isolation.READ_COMMITTED)
  public WeeklyScoreDto updateWeeklyScore(Integer weeklyScoreId) {
    WeeklyScore weeklyScore = weeklyScoreRepository.findById(weeklyScoreId)
            .orElseThrow(() -> new TutorException(WEEKLY_SCORE_NOT_FOUND, weeklyScoreId));

    TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY);
    LocalDate currentWeek = DateHandler.now().with(weekSunday).toLocalDate();

    // Check if weekly score is not from current week
    if (!weeklyScore.getWeek().isEqual(currentWeek)) {
      throw new TutorException(UPDATE_WEEKLY_SCORE_NOT_POSSIBLE, DateHandler.toISOString(weeklyScore.getWeek().atStartOfDay()));
    }

    weeklyScore.computeStatistics();

    return new WeeklyScoreDto(weeklyScore);
  }

  @Retryable(
          value = {SQLException.class},
          backoff = @Backoff(delay = 5000))
  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void removeWeeklyScore(Integer weeklyScoreId) {
    WeeklyScore weeklyScore = weeklyScoreRepository.findById(weeklyScoreId)
            .orElseThrow(() -> new TutorException(WEEKLY_SCORE_NOT_FOUND, weeklyScoreId));

    /* Todo */
  }
}
