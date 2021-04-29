package pt.ulisboa.tecnico.socialsoftware.tournament.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.common.security.UserInfo;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tournament.repository.TournamentRepository;

import java.io.Serializable;
//TODO: Solve this
@Component
public class TournamentPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        UserInfo userInfo = ((UserInfo) authentication.getPrincipal());
        int userId = userInfo.getId();

        if (targetDomainObject instanceof Integer) {
            int id = (int) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "TOURNAMENT.ACCESS":
                    Integer courseExecutionId = tournamentRepository.findCourseExecutionIdByTournamentId(id).orElse(null);
                    if (courseExecutionId != null) {
                        //return userHasThisExecution(userId, courseExecutionId);
                        return true;
                    }
                    return false;
                case "TOURNAMENT.PARTICIPANT":
                    return userParticipatesInTournament(userId, id);
                case "TOURNAMENT.OWNER":
                    Tournament tournament = tournamentRepository.findById(id).orElse(null);
                    if (tournament != null) {
                        return tournament.isCreator(userId);
                    }
                return false;
                    default: return false;
            }
        }

        return false;
    }

    private boolean userParticipatesInTournament(int userId, int tournamentId) {
        return tournamentRepository.countUserTournamentPairById(userId, tournamentId) == 1;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }
}
