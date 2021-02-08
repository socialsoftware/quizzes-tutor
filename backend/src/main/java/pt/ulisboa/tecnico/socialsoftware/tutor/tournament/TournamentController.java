package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @PostMapping(value = "/tournaments/{executionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDto createTournament(Principal principal, @Valid @RequestBody TournamentDto tournamentDto, @PathVariable int executionId, @RequestParam Set<Integer> topicsId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        formatDates(tournamentDto);

        return tournamentService.createTournament(user.getId(), executionId, topicsId, tournamentDto);
    }

    @GetMapping(value = "/tournaments/{executionId}/getTournaments")
    @PreAuthorize("(hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')) and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> getTournamentsForCourseExecution(@PathVariable int executionId) {
        return tournamentService.getTournamentsForCourseExecution(executionId);
    }

    @GetMapping(value = "/tournaments/{executionId}/getOpenTournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> getOpenedTournamentsForCourseExecution(@PathVariable int executionId) {
        return tournamentService.getOpenedTournamentsForCourseExecution(executionId);
    }

    @GetMapping(value = "/tournaments/{executionId}/getClosedTournaments")
    @PreAuthorize("(hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')) and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> getClosedTournamentsForCourseExecution(@PathVariable int executionId) {
        return tournamentService.getClosedTournamentsForCourseExecution(executionId);
    }

    @GetMapping(value = "/tournaments/{executionId}/tournament/{tournamentId}")
    @PreAuthorize("(hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')) and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDto getTournament(@PathVariable int executionId, @PathVariable Integer tournamentId) {
        return tournamentService.getTournament(tournamentId);
    }

    @PutMapping(value = "/tournaments/{executionId}/joinTournament/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.ACCESS')")
    public void joinTournament(Principal principal, @PathVariable int executionId, @PathVariable Integer tournamentId, @RequestParam String password) {
        User user = (User) ((Authentication) principal).getPrincipal();

        tournamentService.joinTournament(user.getId(), tournamentId, password);
    }

    @PutMapping(value = "/tournaments/{executionId}/solveQuiz/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.PARTICIPANT')")
    public StatementQuizDto solveQuiz(Principal principal, @PathVariable int executionId, @PathVariable Integer tournamentId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        return tournamentService.solveQuiz(user.getId(), tournamentId);
    }

    @PutMapping(value = "/tournaments/{executionId}/leaveTournament/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.PARTICIPANT')")
    public void leaveTournament(Principal principal, @PathVariable int executionId, @PathVariable Integer tournamentId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        tournamentService.leaveTournament(user.getId(), tournamentId);
    }

    @PutMapping(value = "/tournaments/{executionId}/updateTournament")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentDto.getId(), 'TOURNAMENT.OWNER')")
    public void updateTournament(@Valid @RequestBody TournamentDto tournamentDto, @PathVariable int executionId, @RequestParam Set<Integer> topicsId) {
        tournamentService.updateTournament(topicsId, tournamentDto);
    }

    @PutMapping(value = "/tournaments/{executionId}/cancelTournament/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.OWNER')")
    public void cancelTournament(@PathVariable int executionId, @PathVariable Integer tournamentId) {
        tournamentService.cancelTournament(tournamentId);
    }

    @DeleteMapping(value = "/tournaments/{executionId}/removeTournament/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.OWNER')")
    public void removeTournament(@PathVariable int executionId, @PathVariable Integer tournamentId) {
        tournamentService.removeTournament(tournamentId);
    }

    private void formatDates(TournamentDto tournamentDto) {
        if (tournamentDto.getStartTime() != null && !DateHandler.isValidDateFormat(tournamentDto.getStartTime()))
            tournamentDto.setStartTime(DateHandler.toISOString(DateHandler.toLocalDateTime(tournamentDto.getStartTime())));

        if (tournamentDto.getEndTime() !=null && !DateHandler.isValidDateFormat(tournamentDto.getEndTime()))
            tournamentDto.setEndTime(DateHandler.toISOString(DateHandler.toLocalDateTime(tournamentDto.getEndTime())));
    }
}