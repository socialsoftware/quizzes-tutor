package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.WeeklyScore;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.WeeklyScoreDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.WeeklyScoreRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class WeeklyScoreService {

    @Autowired
    private WeeklyScoreRepository weeklyScoreRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public WeeklyScoreDto createWeeklyScore(Integer dashboardId) {
        if (dashboardId == null) {
          throw new TutorException(DASHBOARD_NOT_FOUND);
        }

        Dashboard dashboard = dashboardRepository.findById(dashboardId)
              .orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));

        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY);
        LocalDate week = DateHandler.now().with(weekSunday).toLocalDate();

        WeeklyScore weeklyScore = new WeeklyScore(dashboard, week);
        weeklyScoreRepository.save(weeklyScore);

        return new WeeklyScoreDto(weeklyScore);
      }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<WeeklyScoreDto> getWeeklyScores(Integer dashboardId) {
        if (dashboardId == null) {
          throw new TutorException(DASHBOARD_NOT_FOUND);
        }

        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));

        return dashboard.getWeeklyScores().stream()
                .sorted(Comparator.comparing(WeeklyScore::getWeek, Comparator.reverseOrder()))
                .map(WeeklyScoreDto::new)
                .collect(Collectors.toList());
      }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateWeeklyScore(Integer dashboardId) {
        if (dashboardId == null) {
            throw new TutorException(DASHBOARD_NOT_FOUND);
        }

        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));

        LocalDateTime now = DateHandler.now();

        createMissingWeeklyScores(dashboard, now);

        computeStatistics(dashboard);

        removeEmptyClosedWeeklyScores(dashboard);

        dashboard.setLastCheckWeeklyScores(now);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeWeeklyScore(Integer weeklyScoreId) {
        if (weeklyScoreId == null) {
            throw new TutorException(WEEKLY_SCORE_NOT_FOUND);
        }

        WeeklyScore weeklyScore = weeklyScoreRepository.findById(weeklyScoreId)
                .orElseThrow(() -> new TutorException(WEEKLY_SCORE_NOT_FOUND, weeklyScoreId));

        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY);
        LocalDate currentWeek = DateHandler.now().with(weekSunday).toLocalDate();

        if (weeklyScore.getWeek().isEqual(currentWeek)) {
            throw new TutorException(CANNOT_REMOVE_WEEKLY_SCORE);
        }

        weeklyScore.remove();
        weeklyScoreRepository.delete(weeklyScore);
    }

    private void createMissingWeeklyScores(Dashboard dashboard, LocalDateTime now) {
        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY);
        LocalDate currentWeek = now.with(weekSunday).toLocalDate();

        if (dashboard.getLastCheckWeeklyScores() == null) {
            WeeklyScore weeklyScore = new WeeklyScore(dashboard, currentWeek);
            weeklyScoreRepository.save(weeklyScore);
        }

        LocalDateTime lastCheckDate = getLastCheckDate(dashboard, now);

        while (lastCheckDate.isBefore(currentWeek.atStartOfDay())) {
            LocalDate week = lastCheckDate.with(weekSunday).toLocalDate();

            WeeklyScore weeklyScore = new WeeklyScore(dashboard, week);
            weeklyScoreRepository.save(weeklyScore);

            lastCheckDate = lastCheckDate.plusDays(7);
        }
    }

    private LocalDateTime getLastCheckDate(Dashboard dashboard, LocalDateTime now) {
        LocalDateTime startCheckDate;
        if (dashboard.getLastCheckWeeklyScores() == null) {
            startCheckDate = dashboard.getStudent().getQuizAnswers().stream()
                    .filter(quizAnswer -> quizAnswer.getQuiz().getCourseExecution() == dashboard.getCourseExecution())
                    .map(QuizAnswer::getCreationDate)
                    .sorted()
                    .findFirst()
                    .orElse(now);
        } else {
            startCheckDate = dashboard.getLastCheckWeeklyScores();
        }

        return startCheckDate;
    }

    private void computeStatistics(Dashboard dashboard) {
        dashboard.getWeeklyScores().stream()
                .filter(Predicate.not(WeeklyScore::isClosed))
                .forEach(weeklyScore -> weeklyScore.computeStatistics());
    }

    private void removeEmptyClosedWeeklyScores(Dashboard dashboard) {
        Set<WeeklyScore> weeklyScoresToDelete = dashboard.getWeeklyScores().stream()
                .filter(weeklyScore -> weeklyScore.isClosed() && weeklyScore.getNumberAnswered() == 0)
                .collect(Collectors.toSet());

        weeklyScoresToDelete.forEach(weeklyScore -> {
            weeklyScore.remove();
            weeklyScoreRepository.delete(weeklyScore);
        });
    }
}