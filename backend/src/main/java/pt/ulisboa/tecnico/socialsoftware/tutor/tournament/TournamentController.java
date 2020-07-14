package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
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
        User user = checkUser(principal);
        formatDates(tournamentDto);

        return tournamentService.createTournament(user.getId(), topicsId, tournamentDto);
    }

    @GetMapping(value = "/tournaments/getAllTournaments")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public List<TournamentDto> getAllTournaments(Principal principal) {
        User user = checkUser(principal);

        return tournamentService.getAllTournaments(user);
    }

    @GetMapping(value = "/tournaments/getOpenTournaments")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public List<TournamentDto> getOpenTournaments(Principal principal) {
        User user = checkUser(principal);

        return tournamentService.getOpenedTournaments(user);
    }

    @GetMapping(value = "/tournaments/getClosedTournaments")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public List<TournamentDto> getClosedTournaments(Principal principal) {
        User user = checkUser(principal);

        return tournamentService.getClosedTournaments(user);
    }

    @GetMapping(value = "/tournaments/getUserTournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<TournamentDto> getUserTournaments(Principal principal) {
        User user = checkUser(principal);

        return tournamentService.getUserTournaments(user);
    }

    @PutMapping(value = "/tournaments/joinTournament")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public void joinTournament(Principal principal, @Valid @RequestBody TournamentDto tournamentDto, @RequestParam String password) {
        User user = checkUser(principal);

        tournamentService.joinTournament(user.getId(), tournamentDto, password);
    }

    @PutMapping(value = "/tournaments/solveQuiz")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public StatementQuizDto solveQuiz(Principal principal, @Valid @RequestBody TournamentDto tournamentDto) {
        User user = checkUser(principal);

        return tournamentService.solveQuiz(user.getId(), tournamentDto);
    }

    @PutMapping(value = "/tournaments/leaveTournament")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public void leaveTournament(Principal principal, @Valid @RequestBody TournamentDto tournamentDto) {
        User user = checkUser(principal);

        tournamentService.leaveTournament(user.getId(), tournamentDto);
    }

    @PutMapping(value = "/tournaments/updateTournament")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public void updateTournament(Principal principal, @Valid @RequestBody TournamentDto tournamentDto, @RequestParam Set<Integer> topicsId) {
        User user = checkUser(principal);

        tournamentService.updateTournament(user.getId(), topicsId, tournamentDto);
    }

    private User checkUser(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return user;
    }

    private void formatDates(TournamentDto tournamentDto) {
        if (tournamentDto.getStartTime() != null && !DateHandler.isValidDateFormat(tournamentDto.getStartTime()))
            tournamentDto.setStartTime(DateHandler.toISOString(DateHandler.toLocalDateTime(tournamentDto.getStartTime())));

        if (tournamentDto.getEndTime() !=null && !DateHandler.isValidDateFormat(tournamentDto.getEndTime()))
            tournamentDto.setEndTime(DateHandler.toISOString(DateHandler.toLocalDateTime(tournamentDto.getEndTime())));
    }
}