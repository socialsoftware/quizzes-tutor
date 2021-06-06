package pt.ulisboa.tecnico.socialsoftware.tournament.services.local;

import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import org.hibernate.exception.LockAcquisitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.createTournament.CreateTournamentSaga;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.createTournament.CreateTournamentSagaData;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.removeTournament.RemoveTournamentSaga;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.removeTournament.RemoveTournamentSagaData;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.updateTournament.UpdateTournamentSaga;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.updateTournament.UpdateTournamentSagaData;
import pt.ulisboa.tecnico.socialsoftware.tournament.services.remote.TournamentRequiredService;
import pt.ulisboa.tecnico.socialsoftware.common.utils.DateHandler;

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


    public TournamentDto createTournament(Integer userId, Integer executionId, Set<Integer> topicsId, TournamentDto tournamentDto) {
        checkInput(userId, topicsId, tournamentDto);

        TournamentCreator creator = tournamentRequiredService.getTournamentCreator(userId);
        TournamentCourseExecution tournamentCourseExecution = tournamentRequiredService.getTournamentCourseExecution(executionId);
        Set<TournamentTopic> topics = tournamentRequiredService.getTournamentTopics(new TopicListDto(topicsId));

        if (topics.isEmpty()) {
            throw new TutorException(TOURNAMENT_MISSING_TOPICS);
        }

        Tournament tournament = createTournamentSaga(tournamentDto, creator, tournamentCourseExecution, topics);

        // Waits for saga to finish
        Tournament tournamentFinal = tournamentRepository.findById(tournament.getId()).get();
        while (!(tournamentFinal.getState().equals(TournamentState.APPROVED))) {
            tournamentFinal = tournamentRepository.findById(tournament.getId()).get();
        }

        return tournament.getDto();
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Tournament createTournamentSaga(TournamentDto tournamentDto, TournamentCreator creator, TournamentCourseExecution tournamentCourseExecution, Set<TournamentTopic> topics) {
        Tournament tournament = new Tournament(creator, tournamentCourseExecution, topics, tournamentDto);
        tournamentRepository.save(tournament);

        CreateTournamentSagaData data = new CreateTournamentSagaData(tournament.getId(), tournament.getCreator().getId(),
                tournament.getCourseExecution().getId(), tournament.getExternalStatementCreationDto());
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
        return tournamentRepository.findById(tournamentId)
                .map(Tournament::getDto)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
    }


    @Retryable(value = { SQLException.class}, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void joinTournament(Integer userId, Integer tournamentId, String password) {
        TournamentParticipant participant = tournamentRequiredService.getTournamentParticipant(userId);

        Tournament tournament = checkTournament(tournamentId);
        tournament.addParticipant(participant, password);
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

    public TournamentDto updateTournament(Set<Integer> topicsId, TournamentDto tournamentDto) {
        Tournament tournament = checkTournament(tournamentDto.getId());

        tournament.checkCanChange();

        Set<TournamentTopic> topics = tournamentRequiredService.getTournamentTopics(new TopicListDto(topicsId));
        QuizDto quizDto = tournamentRequiredService.getQuiz(tournamentDto.getQuizId());
        quizDto.setNumberOfQuestions(tournamentDto.getNumberOfQuestions());

        updateTournamentQuizSaga(tournament.getId(), quizDto, topics, tournamentDto);

        return tournament.getDto();
    }

    @Transactional
    public void updateTournamentQuizSaga(Integer tournamentId, QuizDto quizDto, Set<TournamentTopic> topics,
                                     TournamentDto tournamentDto) {
        UpdateTournamentSagaData data = new UpdateTournamentSagaData(tournamentId, quizDto, topics, tournamentDto);
        sagaInstanceFactory.create(updateTournamentSaga, data);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto cancelTournament(Integer tournamentId) {
        Tournament tournament = checkTournament(tournamentId);

        tournament.cancel();

        return tournament.getDto();
    }

    public void removeTournament(Integer tournamentId) {
        Tournament tournament = checkTournament(tournamentId);
        tournament.checkCanChange();

        removeTournamentSaga(tournament.getId(), tournament.getQuizId());

        tournamentRepository.delete(tournament);
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
        return tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoTournaments() {
        Integer demoCourseExecutionId = tournamentRequiredService.getDemoCourseExecutionId();

        tournamentRepository.getTournamentsForCourseExecution(demoCourseExecutionId)
            .forEach(tournament -> tournamentRepository.delete(tournament));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void beginRemove(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.beginRemoveTournament();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void confirmRemove(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.confirmRemoveTournament();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void undoRemove(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.undoRemoveTournament();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void beginUpdate(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.beginUpdateQuiz();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void undoUpdate(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.undoUpdateQuiz();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void confirmUpdate(Integer tournamentId, TournamentDto tournamentDto, Set<TournamentTopic> topics) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.confirmUpdateQuiz(tournamentDto, topics);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void confirmCreate(Integer tournamentId, Integer quizId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.confirmTournament(quizId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void rejectCreate(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.rejectTournament();
    }
}
