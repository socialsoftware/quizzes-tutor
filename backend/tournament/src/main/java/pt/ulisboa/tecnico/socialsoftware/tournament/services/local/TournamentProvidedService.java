package pt.ulisboa.tecnico.socialsoftware.tournament.services.local;

import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.RemoteAccessException;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.common.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.createTournament.CreateTournamentSaga;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.createTournament.CreateTournamentSagaData;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.removeTournament.RemoveTournamentSaga;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.removeTournament.RemoveTournamentSagaData;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.updateTournament.UpdateTournamentSaga;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.updateTournament.UpdateTournamentSagaData;
import pt.ulisboa.tecnico.socialsoftware.tournament.services.remote.TournamentRequiredService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.*;

@Service
public class TournamentProvidedService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentRequiredService tournamentRequiredService;

    @Autowired
    private SagaInstanceFactory sagaInstanceFactory;

    @Autowired
    private UpdateTournamentSaga updateTournamentSaga;

    @Autowired
    private CreateTournamentSaga createTournamentSaga;

    @Autowired
    private RemoveTournamentSaga removeTournamentSaga;

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(Integer userId, Integer executionId, Set<Integer> topicsId,
                                          TournamentDto tournamentDto, String username, String name) {
        checkInput(userId, topicsId, tournamentDto);

        Tournament tournament = new Tournament(new TournamentCreator(userId, username, name), tournamentDto);
        tournamentRepository.save(tournament);

        CreateTournamentSagaData data = new CreateTournamentSagaData(tournament.getId(), tournament.getCreator().getId(),
                executionId, tournament.getExternalStatementCreationDto(), new TopicListDto(topicsId));
        sagaInstanceFactory.create(createTournamentSaga, data);

        return tournament.getDto();
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getTournamentsForCourseExecution(Integer executionId) {
        return tournamentRepository.getTournamentsForCourseExecution(executionId).stream()
                .filter(t -> t.getState().equals(TournamentState.APPROVED))
                .map(Tournament::getDto)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getOpenedTournamentsForCourseExecution(Integer executionId) {
        LocalDateTime now = DateHandler.now();
        return tournamentRepository.getOpenedTournamentsForCourseExecution(executionId, now).stream()
                .filter(t -> t.getState().equals(TournamentState.APPROVED))
                .map(Tournament::getDto)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getClosedTournamentsForCourseExecution(Integer executionId) {
        LocalDateTime now = DateHandler.now();
        return tournamentRepository.getClosedTournamentsForCourseExecution(executionId, now).stream()
                .filter(t -> t.getState().equals(TournamentState.APPROVED))
                .map(Tournament::getDto)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto getTournament(Integer tournamentId) {
        return tournamentRepository.findById(tournamentId)
                .filter(t -> t.getState().equals(TournamentState.APPROVED))
                .map(Tournament::getDto)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
    }

    @Retryable(value = { SQLException.class, TutorException.class}, backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void joinTournament(Integer userId, Integer tournamentId, String password, String username, String name) {
        Tournament tournament = checkTournament(tournamentId);

        if (tournament.getState().equals(TournamentState.APPROVED)) {
            tournament.addParticipant(new TournamentParticipant(userId, username, name), password);
        }
        else {
            throw new TutorException(UNSUPPORTED_STATE, tournament.getState().toString());
        }

    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatementQuizDto solveQuiz(Integer userId, Integer tournamentId) {
        Tournament tournament = checkTournament(tournamentId);
        StatementQuizDto statementQuizDto = tournamentRequiredService.startTournamentQuiz(userId, tournament.getQuizId());

        tournament.findParticipant(userId).setAnswered(true);

        return statementQuizDto;
    }

    @Retryable(value = { SQLException.class, TutorException.class }, backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void leaveTournament(Integer userId, Integer tournamentId) {
        Tournament tournament = checkTournament(tournamentId);

        if (tournament.getState().equals(TournamentState.APPROVED)) {
            TournamentParticipant participant = tournament.findParticipant(userId);
            tournament.removeParticipant(participant);
        }
        else {
            throw new TutorException(UNSUPPORTED_STATE, tournament.getState().toString());
        }
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto updateTournament(Set<Integer> topicsId, TournamentDto tournamentDto) {
        Tournament tournament = checkTournament(tournamentDto.getId());
        tournament.checkCanChange();

        UpdateTournamentSagaData data = new UpdateTournamentSagaData(tournament.getId(), tournamentDto, tournament.getDto(),
                new TopicListDto(topicsId), tournament.getTopics(), tournament.getCourseExecution().getId());
        sagaInstanceFactory.create(updateTournamentSaga, data);

        return tournament.getDto();
    }

    @Retryable(value = { SQLException.class,TutorException.class }, backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto cancelTournament(Integer tournamentId) {
        Tournament tournament = checkTournament(tournamentId);

        if (tournament.getState().equals(TournamentState.APPROVED)) {
            tournament.cancel();
        }
        else {
            throw new TutorException(UNSUPPORTED_STATE, tournament.getState().toString());
        }

        return tournament.getDto();
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeTournament(Integer tournamentId) {
        Tournament tournament = checkTournament(tournamentId);

        RemoveTournamentSagaData data = new RemoveTournamentSagaData(tournament.getId(), tournament.getQuizId());
        sagaInstanceFactory.create(removeTournamentSaga, data);
    }

    private void checkInput(Integer userId, Set<Integer> topicsId, TournamentDto tournamentDto) {
        if (userId == null) {
            throw new TutorException(TOURNAMENT_MISSING_USER);
        }
        if (topicsId == null) {
            throw new TutorException(TOURNAMENT_MISSING_TOPICS);
        }
        if (tournamentDto.getStartTime() == null) {
            throw new TutorException(TOURNAMENT_MISSING_START_TIME);
        }
        if (tournamentDto.getEndTime() == null) {
            throw new TutorException(TOURNAMENT_MISSING_END_TIME);
        }
        if (tournamentDto.getNumberOfQuestions() == null) {
            throw new TutorException(TOURNAMENT_MISSING_NUMBER_OF_QUESTIONS);
        }
    }

    private Tournament checkTournament(Integer tournamentId) {
        return tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoTournaments() {
        CourseExecutionDto demoCourseExecution;
        while(true) {
            try {
                demoCourseExecution = tournamentRequiredService.getDemoCourseExecutionId();
                if (demoCourseExecution!= null) {
                    break;
                }
            }
            catch(RemoteAccessException e) {
                continue;
            }
        }

        tournamentRepository.getTournamentsForCourseExecution(demoCourseExecution.getCourseExecutionId()).stream()
            .filter(tournament -> tournament.getState().equals(TournamentState.APPROVED))
            .forEach(tournament -> {
                tournament.remove();
                tournamentRepository.delete(tournament);
            });
    }

    public void undoUpdate(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.undoUpdate();
    }

    public void confirmUpdate(Integer tournamentId, TournamentDto tournamentDto) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.confirmUpdateQuiz(tournamentDto);
    }

    public void confirmCreate(Integer tournamentId, Integer quizId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.confirmTournament(quizId);
    }

    public void rejectCreate(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.rejectTournament();
    }

    public void storeTopics(Integer tournamentId, Set<TournamentTopic> tournamentTopics) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.setTopics(tournamentTopics);
    }

    public void storeCourseExecution(Integer tournamentId, TournamentCourseExecution tournamentCourseExecution) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.setCourseExecution(tournamentCourseExecution);
    }

    public void updateTopics(Integer tournamentId, Set<TournamentTopic> topics) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.updateTopics(topics);
    }

    public void undoUpdateTopics(Integer tournamentId, Set<TournamentTopic> topics) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.setTopics(topics);
    }

    public void beginUpdateTournament(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.beginUpdateTournament();
    }

    public void beginRemoveTournament(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.beginRemoveTournament();
    }

    public void confirmRemoveTournament(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.confirmRemoveTournament();
    }

    public void undoRemoveTournament(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.undoRemoveTournament();
    }
}
