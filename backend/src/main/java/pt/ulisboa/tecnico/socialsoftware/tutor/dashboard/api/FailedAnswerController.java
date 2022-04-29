package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.FailedAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.services.FailedAnswerService;

import java.util.List;

@RestController
public class FailedAnswerController {
    @Autowired
    private FailedAnswerService failedAnswerService;

    FailedAnswerController(FailedAnswerService failedAnswerService) {
        this.failedAnswerService = failedAnswerService;
    }

    @PutMapping("/students/dashboards/{dashboardId}/failedanswers")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#dashboardId, 'DASHBOARD.ACCESS')")
    public List<FailedAnswerDto> updateFailedAnswers(@PathVariable int dashboardId) {
        return this.failedAnswerService.updateFailedAnswers(dashboardId);
    }

    @DeleteMapping("/students/failedanswers/{failedAnswerId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#failedAnswerId, 'FAILEDANSWER.ACCESS')")
    public void removeFailedAnswer(@PathVariable int failedAnswerId) {
        this.failedAnswerService.removeFailedAnswer(failedAnswerId);
    }
}