package pt.ulisboa.tecnico.socialsoftware.tournament.services.local;

import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.removeTournament.RemoveTournamentSaga;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.removeTournament.RemoveTournamentSagaData;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.solveTournamentQuiz.CreateAndSolveTournamentQuizSaga;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.solveTournamentQuiz.CreateAndSolveTournamentQuizSagaData;
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

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentRequiredService tournamentRequiredService;

    @Autowired
    private SagaInstanceFactory sagaInstanceFactory;

    @Autowired
    private RemoveTournamentSaga removeTournamentSaga;

    @Autowired
    private CreateAndSolveTournamentQuizSaga solveTournamentQuizSaga;

    @Autowired
    private UpdateTournamentSaga updateTournamentSaga;

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(Integer userId, Integer executionId, Set<Integer> topicsId, TournamentDto tournamentDto) {
        checkInput(userId, topicsId, tournamentDto);

        TournamentCreator creator = tournamentRequiredService.getTournamentCreator(userId);
        TournamentCourseExecution tournamentCourseExecution = tournamentRequiredService.getTournamentCourseExecution(executionId);

        Set<TournamentTopic> topics = tournamentRequiredService.getTournamentTopics(new TopicListDto(topicsId));

        if (topics.isEmpty()) {
            throw new TutorException(TOURNAMENT_MISSING_TOPICS);
        }

        Tournament tournament = new Tournament(creator, tournamentCourseExecution, topics, tournamentDto);
        tournamentRepository.save(tournament);

        return tournament.getDto();
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


    public void joinTournament(Integer userId, Integer tournamentId, String password) {
        TournamentParticipant participant = tournamentRequiredService.getTournamentParticipant(userId);

        extracted(tournamentId, password, participant);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void extracted(Integer tournamentId, String password, TournamentParticipant participant) {
        Tournament tournament = checkTournament(tournamentId);
        tournament.addParticipant(participant, password);
    }

    public StatementQuizDto solveQuiz(Integer userId, Integer tournamentId) {
        Tournament tournament = checkTournament(tournamentId);

        StatementQuizDto dto;
        if (!tournament.hasQuiz()) {
            Tournament tournamentUpdated = createAndSolveTournamentQuiz(tournament, userId);
            dto = tournamentRequiredService.getStatementQuiz(userId, tournamentUpdated.getQuizId());
        }
        else {
            dto = tournamentRequiredService.startTournamentQuiz(userId, tournament.getQuizId());
        }

        return dto;
    }

    @Transactional
    public void createAndSolveTournamentQuizSaga(Integer tournamentId, Integer quizId, Integer userId,
                                                 Integer courseExecutionId, ExternalStatementCreationDto quizForm) {
        CreateAndSolveTournamentQuizSagaData data = new CreateAndSolveTournamentQuizSagaData(tournamentId, quizId,
                userId, courseExecutionId, quizForm);
        sagaInstanceFactory.create(solveTournamentQuizSaga, data);
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

        /*tournament.updateTournament(tournamentDto, topics);

        if (tournament.hasQuiz()) {
            QuizDto quizDto = tournamentRequiredService.getQuiz(tournamentDto.getQuizId());
            quizDto.setNumberOfQuestions(tournamentDto.getNumberOfQuestions());

            tournamentRequiredService.updateQuiz(quizDto);
        }*/

        if (tournament.hasQuiz()) {
            QuizDto quizDto = tournamentRequiredService.getQuiz(tournamentDto.getQuizId());
            quizDto.setNumberOfQuestions(tournamentDto.getNumberOfQuestions());

            updateTournamentQuizSaga(tournament.getId(), quizDto, topics, tournamentDto);
        }
        else {
            tournament.updateTournament(tournamentDto, topics);
        }

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

        if (tournament.getQuizId() != null) {
            removeTournamentSaga(tournament.getId(), tournament.getQuizId());
        }
        else {
            tournament.remove();
        }

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

    private Tournament createAndSolveTournamentQuiz(Tournament tournament, Integer userId) {
        ExternalStatementCreationDto quizForm = tournament.getExternalStatementCreationDto();

        tournament.setState(TournamentState.READY_FOR_UPDATE);
        tournamentRepository.save(tournament);

        createAndSolveTournamentQuizSaga(tournament.getId(), tournament.getQuizId(), userId,
                tournament.getCourseExecution().getId(), quizForm);

        // Waits for saga to finish
        Tournament tournamentFinal = tournamentRepository.findById(tournament.getId()).get();
        while (!(tournamentFinal.getState().equals(TournamentState.APPROVED))) {
            tournamentFinal = tournamentRepository.findById(tournament.getId()).get();
        }

        /*Integer quizId = tournamentRequiredService.createQuiz(tournament.getCreator().getId(),
                tournament.getCourseExecution().getId(), quizForm);
        tournament.setQuizId(quizId);*/

        return tournamentFinal;
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

    public void beginSolve(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.beginSolveQuiz();
    }

    public void undoSolve(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.undoSolveQuiz();
    }

    public void confirmSolve(Integer tournamentId, Integer quizId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournament.confirmSolveQuiz(quizId);
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
}
