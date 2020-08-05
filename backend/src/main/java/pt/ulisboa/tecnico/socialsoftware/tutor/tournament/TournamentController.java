package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @PostMapping(value = "/tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(tournamentDto.getId(), 'TOURNAMENT.ACCESS')")
    public TournamentDto createTournament(Principal principal, @Valid @RequestBody TournamentDto tournamentDto, @RequestParam Set<Integer> topicsId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        formatDates(tournamentDto);

        return tournamentService.createTournament(user.getId(), topicsId, tournamentDto);
    }

    @GetMapping(value = "/tournaments/getAllTournaments")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')")
    public List<TournamentDto> getAllTournaments(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        return tournamentService.getAllTournaments(user);
    }

    @GetMapping(value = "/tournaments/getOpenTournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<TournamentDto> getOpenTournaments(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        return tournamentService.getOpenedTournaments(user);
    }

    @GetMapping(value = "/tournaments/getClosedTournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<TournamentDto> getClosedTournaments(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        return tournamentService.getClosedTournaments(user);
    }

    @GetMapping(value = "/tournaments/getUserTournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<TournamentDto> getUserTournaments(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        return tournamentService.getUserTournaments(user);
    }

    @PutMapping(value = "/tournaments/joinTournament")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(tournamentDto.getId(), 'TOURNAMENT.ACCESS')")
    public void joinTournament(Principal principal, @Valid @RequestBody TournamentDto tournamentDto, @RequestParam String password) {
        User user = (User) ((Authentication) principal).getPrincipal();

        tournamentService.joinTournament(user.getId(), tournamentDto, password);
    }

    @PutMapping(value = "/tournaments/solveQuiz")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(tournamentDto.getId(), 'TOURNAMENT.ACCESS')")
    public StatementQuizDto solveQuiz(Principal principal, @Valid @RequestBody TournamentDto tournamentDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        return tournamentService.solveQuiz(user.getId(), tournamentDto);
    }

    @PutMapping(value = "/tournaments/leaveTournament")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(tournamentDto.getId(), 'TOURNAMENT.ACCESS')")
    public void leaveTournament(Principal principal, @Valid @RequestBody TournamentDto tournamentDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        tournamentService.leaveTournament(user.getId(), tournamentDto);
    }

    @PutMapping(value = "/tournaments/updateTournament")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(tournamentDto.getId(), 'TOURNAMENT.OWNER')")
    public void updateTournament(Principal principal, @Valid @RequestBody TournamentDto tournamentDto, @RequestParam Set<Integer> topicsId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        tournamentService.updateTournament(user.getId(), topicsId, tournamentDto);
    }

    @PutMapping(value = "/tournaments/cancelTournament")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(tournamentDto.getId(), 'TOURNAMENT.OWNER')")
    public void cancelTournament(Principal principal, @Valid @RequestBody TournamentDto tournamentDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        tournamentService.cancelTournament(user.getId(), tournamentDto);
    }

    @DeleteMapping(value = "/tournaments/removeTournament/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.OWNER')")
    public void removeTournament(Principal principal, @PathVariable Integer tournamentId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        tournamentService.removeTournament(user.getId(), tournamentId);
    }

    private void formatDates(TournamentDto tournamentDto) {
        if (tournamentDto.getStartTime() != null && !DateHandler.isValidDateFormat(tournamentDto.getStartTime()))
            tournamentDto.setStartTime(DateHandler.toISOString(DateHandler.toLocalDateTime(tournamentDto.getStartTime())));

        if (tournamentDto.getEndTime() !=null && !DateHandler.isValidDateFormat(tournamentDto.getEndTime()))
            tournamentDto.setEndTime(DateHandler.toISOString(DateHandler.toLocalDateTime(tournamentDto.getEndTime())));
    }
}