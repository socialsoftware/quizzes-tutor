package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.WeeklyScoreDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.services.WeeklyScoreService;

import java.util.List;

@RestController
public class WeeklyScoreController {
    @Autowired
    private WeeklyScoreService weeklyScoreService;

    WeeklyScoreController(WeeklyScoreService weeklyScoreService) {
        this.weeklyScoreService = weeklyScoreService;
    }

    @PutMapping("/students/dashboards/{dashboardId}/weeklyscores")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#dashboardId, 'DASHBOARD.ACCESS')")
    public List<WeeklyScoreDto> updateWeeklyScores(@PathVariable Integer dashboardId) {
        return weeklyScoreService.updateWeeklyScore(dashboardId);
    }

}