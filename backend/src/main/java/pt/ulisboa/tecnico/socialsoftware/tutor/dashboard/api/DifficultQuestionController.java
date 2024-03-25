package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DifficultQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.services.DifficultQuestionService;

import java.security.Principal;
import java.util.List;

@RestController
public class DifficultQuestionController {
    @Autowired
    private DifficultQuestionService difficultQuestionService;

    DifficultQuestionController(DifficultQuestionService difficultQuestionService) {
        this.difficultQuestionService = difficultQuestionService;
    }

    @PutMapping("/executions/{courseExecutionId}/difficultquestions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS')")
    public void updateCourseExecutionDifficultQuestions(@PathVariable int courseExecutionId) {
        this.difficultQuestionService.updateCourseExecutionWeekDifficultQuestions(courseExecutionId);
    }

    @PutMapping("/students/dashboards/{dashboardId}/difficultquestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#dashboardId, 'DASHBOARD.ACCESS')")
    public List<DifficultQuestionDto> updateDashboardDifficultQuestions(@PathVariable int dashboardId) {
        return this.difficultQuestionService.updateDashboardDifficultQuestions(dashboardId);
    }

    @DeleteMapping("/students/difficultquestions/{difficultQuestionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#difficultQuestionId, 'DIFFICULTQUESTION.ACCESS')")
    public void deleteDifficultQuestions(Principal principal, @PathVariable int difficultQuestionId) {
        int studentId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        
        this.difficultQuestionService.removeDifficultQuestion(studentId, difficultQuestionId);
    }
}