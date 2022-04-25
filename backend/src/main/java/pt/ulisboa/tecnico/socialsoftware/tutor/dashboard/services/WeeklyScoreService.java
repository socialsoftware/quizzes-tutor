package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DASHBOARD_NOT_FOUND;

@Service
public class WeeklyScoreService {

    @Autowired
    private WeeklyScoreRepository weeklyScoreRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<WeeklyScoreDto> updateWeeklyScore(Integer dashboardId) {
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

        return dashboard.getWeeklyScores().stream()
                .sorted(Comparator.comparing(WeeklyScore::getWeek, Comparator.reverseOrder()))
                .map(WeeklyScoreDto::new)
                .collect(Collectors.toList());
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
                    .filter(quizAnswer -> quizAnswer.getCreationDate() != null)
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
                .forEach(weeklyScore -> {
                    LocalDateTime start = weeklyScore.getWeek().atStartOfDay();
                    LocalDateTime end = weeklyScore.getWeek().plusDays(7).atStartOfDay();

                    Set<QuizAnswer> answers = quizAnswerRepository.findByStudentAndCourseExecutionInPeriod(dashboard.getStudent().getId(),
                            dashboard.getCourseExecution().getId(), start, end);

                    weeklyScore.computeStatistics(answers);
                });
    }

    private void removeEmptyClosedWeeklyScores(Dashboard dashboard) {
        Set<WeeklyScore> weeklyScoresToDelete = dashboard.getWeeklyScores().stream()
                .filter(weeklyScore -> weeklyScore.isClosed() && weeklyScore.getQuestionsAnswered() == 0)
                .collect(Collectors.toSet());

        weeklyScoresToDelete.forEach(weeklyScore -> {
            weeklyScore.remove();
            weeklyScoreRepository.delete(weeklyScore);
        });
    }
}