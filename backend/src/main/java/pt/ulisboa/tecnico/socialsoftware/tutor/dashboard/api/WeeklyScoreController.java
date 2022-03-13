package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.DashboardService;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.FailedAnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.WeeklyScoreService;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DashboardDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.WeeklyScoreDto;

import java.security.Principal;
import java.util.List;

@RestController
public class WeeklyScoreController {
    private static final Logger logger = LoggerFactory.getLogger(WeeklyScoreController.class);

    @Autowired
    private WeeklyScoreService weeklyScoreService;

    WeeklyScoreController(WeeklyScoreService weeklyScoreService) {
        this.weeklyScoreService = weeklyScoreService;
    }

    @GetMapping("/students/dashboards/{dashboardId}/weeklyScores")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#dashboardId, 'DASHBOARD.ACCESS')")
    public List<WeeklyScoreDto> getWeeklyScores(@PathVariable Integer dashboardId) {
        return weeklyScoreService.getWeeklyScores(dashboardId);
    }

    @PostMapping("/students/dashboards/{dashboardId}/weeklyScores")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#dashboardId, 'DASHBOARD.ACCESS')")
    public void updateWeeklyScores(@PathVariable Integer dashboardId) {
        weeklyScoreService.updateWeeklyScore(dashboardId);
    }

}