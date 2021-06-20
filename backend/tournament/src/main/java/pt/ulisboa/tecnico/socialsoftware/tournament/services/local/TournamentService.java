package pt.ulisboa.tecnico.socialsoftware.tournament.services.local;

import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuizDto;
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
public class TournamentService {

    private final Logger logger = LoggerFactory.getLogger(TournamentService.class);

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentRequiredService tournamentRequiredService;

    @Autowired
    private SagaInstanceFactory sagaInstanceFactory;

    @Autowired
    private RemoveTournamentSaga removeTournamentSaga;

    @Autowired
    private UpdateTournamentSaga updateTournamentSaga;

    @Autowired
    private CreateTournamentSaga createTournamentSaga;


    public TournamentDto createTournament(Integer userId, Integer executionId, Set<Integer> topicsId,
                                          TournamentDto tournamentDto, String username, String name) {
        Tournament tournament = createTournamentSaga(userId, executionId, topicsId, tournamentDto, username, name);

        // Waits for saga to finish
        Tournament approvedTournament = tournamentRepository.findById(tournament.getId()).get();
        while (!(approvedTournament.getState().equals(TournamentState.APPROVED))) {
            approvedTournament = tournamentRepository.findById(tournament.getId()).get();
        }

        return approvedTournament.getDto();
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Tournament createTournamentSaga(Integer userId, Integer executionId, Set<Integer> topicsId,
                                           TournamentDto tournamentDto, String username, String name) {
        checkInput(userId, topicsId, tournamentDto);

        Tournament tournament = new Tournament(new TournamentCreator(userId, username, name), tournamentDto);
        tournamentRepository.save(tournament);

        CreateTournamentSagaData data = new CreateTournamentSagaData(tournament.getId(), tournament.getCreator().getId(),
                executionId, tournament.getExternalStatementCreationDto(), new TopicListDto(topicsId));
        sagaInstanceFactory.create(createTournamentSaga, data);
        return tournament;
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getTournamentsForCourseExecution(Integer executionId) {
        return tournamentRepository.getTournamentsForCourseExecution(executionId).stream().map(Tournament::getDto)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getOpenedTournamentsForCourseExecution(Integer executionId) {
        LocalDateTime now = DateHandler.now();
        return tournamentRepository.getOpenedTournamentsForCourseExecution(executionId, now).stream().map(Tournament::getDto)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getClosedTournamentsForCourseExecution(Integer executionId) {
        LocalDateTime now = DateHandler.now();
        return tournamentRepository.getClosedTournamentsForCourseExecution(executionId, now).stream().map(Tournament::getDto)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto getTournament(Integer tournamentId) {
        return tournamentRepository.findApprovedTournamentById(tournamentId)
                .map(Tournament::getDto)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
    }

    @Retryable(value = { SQLException.class}, maxAttempts = 6, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void joinTournament(Integer userId, Integer tournamentId, String password, String username, String name) {
        Tournament tournament = checkTournament(tournamentId);
        tournament.addParticipant(new TournamentParticipant(userId, username, name), password);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatementQuizDto solveQuiz(Integer userId, Integer tournamentId) {
        Tournament tournament = checkTournament(tournamentId);
        return tournamentRequiredService.startTournamentQuiz(userId, tournament.getQuizId());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void leaveTournament(Integer userId, Integer tournamentId) {
        Tournament tournament = checkTournament(tournamentId);

        TournamentParticipant participant = tournament.findParticipant(userId);

        tournament.removeParticipant(participant);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto updateTournament(Set<Integer> topicsId, TournamentDto tournamentDto) {
        Tournament tournament = checkTournament(tournamentDto.getId());
        tournament.checkCanChange();

        updateTournamentQuizSaga(tournament.getId(), tournamentDto, new TopicListDto(topicsId));

        return tournament.getDto();
    }

    @Transactional
    public void updateTournamentQuizSaga(Integer tournamentId,
                                     TournamentDto tournamentDto, TopicListDto topicsList) {
        UpdateTournamentSagaData data = new UpdateTournamentSagaData(tournamentId, tournamentDto,
                topicsList);
        sagaInstanceFactory.create(updateTournamentSaga, data);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto cancelTournament(Integer tournamentId) {
        Tournament tournament = checkTournament(tournamentId);

        tournament.cancel();

        return tournament.getDto();
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeTournament(Integer tournamentId) {
        Tournament tournament = checkTournament(tournamentId);
        tournament.checkCanChange();

        removeTournamentSaga(tournament.getId(), tournament.getQuizId());

        // Waits for saga to finish
        Tournament removedTournament = tournamentRepository.findById(tournament.getId()).get();
        while (!(removedTournament.getState().equals(TournamentState.REMOVED))) {
            removedTournament = tournamentRepository.findById(tournament.getId()).get();
        }

        tournamentRepository.delete(removedTournament);
    }

    @Transactional
    public void removeTournamentSaga(Integer tournamentId, Integer quizId) {
        RemoveTournamentSagaData data = new RemoveTournamentSagaData(tournamentId, quizId);
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
        return tournamentRepository.findApprovedTournamentById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoTournaments() {
        Integer demoCourseExecutionId;
        while(true) {
            try {
                demoCourseExecutionId = tournamentRequiredService.getDemoCourseExecutionId();
                if (demoCourseExecutionId != null) {
                    break;
                }
            }
            catch(RemoteAccessException e) {
                continue;
            }
        }

        tournamentRepository.getTournamentsForCourseExecution(demoCourseExecutionId)
            .forEach(tournament -> {
                tournament.remove();
                tournamentRepository.delete(tournament);
            });
    }

    public void beginRemove(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.beginRemoveTournament();
    }

    public void confirmRemove(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.confirmRemoveTournament();
    }

    public void undoRemove(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.undoRemoveTournament();
    }

    public void beginUpdate(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.beginUpdateQuiz();
    }

    public void undoUpdate(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.undoUpdateQuiz();
    }

    public void confirmUpdate(Integer tournamentId, TournamentDto tournamentDto, Set<TournamentTopic> topics) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.confirmUpdateQuiz(tournamentDto, topics);
    }

    public void confirmCreate(Integer tournamentId, Integer quizId, Set<TournamentTopic> topics,
                              TournamentCourseExecution courseExecution) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.confirmTournament(quizId, topics, courseExecution);
    }

    public void rejectCreate(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.rejectTournament();
    }
}
