package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.log.LogService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_STUDENT" })
public class StatsController {

    @Autowired
    private StatsService statsService;

    @Autowired
    private LogService logService;

    @GetMapping("/stats")
    public StatsDto getStats(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            return null;
        }

        logService.create(user, LocalDateTime.now(), "GET_STATS");

        return statsService.getStats(user.getUsername());


    }

}