package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.DifficultQuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DifficultQuestion;

import java.util.List;

@RestController
public class DifficultQuestionController {
    private static final Logger logger = LoggerFactory.getLogger(DifficultQuestionController.class);

    @Autowired
    private DifficultQuestionService difficultQuestionService;

    DifficultQuestionController(DifficultQuestionService difficultQuestionService) {
        this.difficultQuestionService = difficultQuestionService;
    }

    @GetMapping("/students/dashboards/{dashboardId}/difficultquestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#dashboardId, 'DASHBOARD.ACCESS')")
    public List<DifficultQuestionDto> getFailedAnswers(@PathVariable int dashboardId) {
        return this.difficultQuestionService.getDifficultQuestions(dashboardId);
    }

    @PutMapping("/students/dashboards/{dashboardId}/difficultquestions/update")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#dashboardId, 'DASHBOARD.ACCESS')")
    public List<DifficultQuestionDto> updateDifficultQuestions(@PathVariable int dashboardId) {
        return this.difficultQuestionService.updateDifficultQuestions(dashboardId);
    }
}