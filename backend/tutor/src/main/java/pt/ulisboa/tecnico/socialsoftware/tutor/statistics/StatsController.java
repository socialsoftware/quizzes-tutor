package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.common.security.UserInfo;

import java.security.Principal;

@RestController
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/stats/executions/{executionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public StatsDto getStats(Principal principal, @PathVariable int executionId) {
        UserInfo userInfo = (UserInfo) ((Authentication) principal).getPrincipal();

        return statsService.getStats(userInfo.getId(), executionId);
   }
}