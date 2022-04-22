package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.StatsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.services.DashboardService;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DashboardDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;

import java.security.Principal;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private DashboardService dashboardService;

    DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/students/dashboards/executions/{courseExecutionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS')")
    public DashboardDto getDashboard(Principal principal, @PathVariable int courseExecutionId) {
        int studentId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();

        return dashboardService.getDashboard(courseExecutionId, studentId);
    }

    @GetMapping("/students/dashboards/{dashboardId}/stats")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#dashboardId, 'DASHBOARD.ACCESS')")
    public StatsDto getStats(Principal principal, @PathVariable int dashboardId) {
        AuthUser authUser = (AuthUser) ((Authentication) principal).getPrincipal();

        return dashboardService.getStats(dashboardId);
    }

}