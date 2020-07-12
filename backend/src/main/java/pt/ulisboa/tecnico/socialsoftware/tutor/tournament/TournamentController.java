package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @PostMapping(value = "/tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto createTournament(Principal principal, @Valid @RequestBody TournamentDto tournamentDto, @RequestParam Set<Integer> topicsId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        formatDates(tournamentDto);
        return tournamentService.createTournament(user.getId(), topicsId, tournamentDto);
    }

    @GetMapping(value = "/tournaments/getAllTournaments")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public List<TournamentDto> getAllTournaments(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.getAllTournaments(user);
    }

    @GetMapping(value = "/tournaments/getOpenTournaments")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public List<TournamentDto> getOpenTournaments(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.getOpenedTournaments(user);
    }

    @GetMapping(value = "/tournaments/getClosedTournaments")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public List<TournamentDto> getClosedTournaments(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.getClosedTournaments(user);
    }

    @GetMapping(value = "/tournaments/getUserTournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<TournamentDto> getUserTournaments(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.getUserTournaments(user);
    }

    private void formatDates(TournamentDto tournamentDto) {
        if (tournamentDto.getStartTime() != null && !DateHandler.isValidDateFormat(tournamentDto.getStartTime()))
            tournamentDto.setStartTime(DateHandler.toISOString(DateHandler.toLocalDateTime(tournamentDto.getStartTime())));

        if (tournamentDto.getEndTime() !=null && !DateHandler.isValidDateFormat(tournamentDto.getEndTime()))
            tournamentDto.setEndTime(DateHandler.toISOString(DateHandler.toLocalDateTime(tournamentDto.getEndTime())));
    }
}