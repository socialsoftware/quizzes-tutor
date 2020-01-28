package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.security.Principal;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_STUDENT" })
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/executions/{executionId}/stats")
    public StatsDto getStats(Principal principal, @PathVariable int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            return null;
        }

        return statsService.getStats(user.getUsername(), executionId);
    }
}