package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.services.local;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament.dtos.TournamentParticipantDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.services.remote.TournamentRequiredService;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.quiz.dtos.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.answer.dtos.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament.dtos.ExternalStatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament.dtos.TournamentDto;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TournamentProvidedService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentRequiredService tournamentRequiredService;

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(Integer userId, Integer executionId, Set<Integer> topicsId, TournamentDto tournamentDto) {
        checkInput(userId, topicsId, tournamentDto);

        TournamentCreator creator = tournamentRequiredService.getTournamentCreator(userId);
        TournamentCourseExecution tournamentCourseExecution = tournamentRequiredService.getTournamentCourseExecution(executionId);

        Set<TournamentTopic> topics = tournamentRequiredService.getTournamentTopics(topicsId);

        if (topics.isEmpty()) {
            throw new TutorException(TOURNAMENT_MISSING_TOPICS);
        }

        Tournament tournament = new Tournament(creator, tournamentCourseExecution, topics, tournamentDto);
        tournamentRepository.save(tournament);

        return new TournamentDto(tournament);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getTournamentsForCourseExecution(Integer executionId) {
        return tournamentRepository.getTournamentsForCourseExecution(executionId).stream().map(TournamentDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getOpenedTournamentsForCourseExecution(Integer executionId) {
        LocalDateTime now = DateHandler.now();
        return tournamentRepository.getOpenedTournamentsForCourseExecution(executionId, now).stream().map(TournamentDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getClosedTournamentsForCourseExecution(Integer executionId) {
        LocalDateTime now = DateHandler.now();
        return tournamentRepository.getClosedTournamentsForCourseExecution(executionId, now).stream().map(TournamentDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto getTournament(Integer tournamentId) {
        return tournamentRepository.findById(tournamentId)
                .map(TournamentDto::new)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void joinTournament(Integer userId, Integer tournamentId, String password) {
        Tournament tournament = checkTournament(tournamentId);

        TournamentParticipant participant = tournamentRequiredService.getTournamentParticipant(userId);

        tournament.addParticipant(participant, password);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatementQuizDto solveQuiz(Integer userId, Integer tournamentId) {
        Tournament tournament = checkTournament(tournamentId);

        if (!tournament.hasQuiz()) {
            createQuiz(tournament);
        }

        //return answerService.startQuiz(userId, tournament.getQuizId());
        return tournamentRequiredService.startTournamentQuiz(userId, tournament.getQuizId());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void leaveTournament(Integer userId, Integer tournamentId) {
        Tournament tournament = checkTournament(tournamentId);

        TournamentParticipant participant = tournament.findParticipant(userId);

        tournament.removeParticipant(participant);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TournamentDto updateTournament(Set<Integer> topicsId, TournamentDto tournamentDto) {
        Tournament tournament = checkTournament(tournamentDto.getId());

        Set<TournamentTopic> topics = tournamentRequiredService.getTournamentTopics(topicsId);

        tournament.updateTournament(tournamentDto, topics);

        if (tournament.hasQuiz()) { // update current Quiz
            /*Quiz quiz = quizRepository.findById(tournamentDto.getQuizId())
                    .orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, tournamentDto.getQuizId()));

            QuizDto quizDto = quizService.findById(tournamentDto.getQuizId());*/
            QuizDto quizDto = tournamentRequiredService.getQuiz(tournamentDto.getQuizId());
            quizDto.setNumberOfQuestions(tournamentDto.getNumberOfQuestions());

            tournamentRequiredService.updateQuiz(quizDto);
        }

        return new TournamentDto(tournament);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto cancelTournament(Integer tournamentId) {
        Tournament tournament = checkTournament(tournamentId);

        tournament.cancel();

        return new TournamentDto(tournament);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeTournament(Integer tournamentId) {
        Tournament tournament = checkTournament(tournamentId);

        // Pessimistic view e commutative updates
        if(tournament.getQuizId() != null) {
            tournamentRequiredService.deleteQuiz(tournament.getQuizId());
        }

        tournament.remove();

        tournamentRepository.delete(tournament);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentParticipantDto> getTournamentParticipants(TournamentDto tournamentDto) {
        Tournament tournament = checkTournament(tournamentDto.getId());

        return tournament.getParticipants().stream().map(TournamentParticipantDto::new).collect(Collectors.toList());
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

    private void createQuiz(Tournament tournament) {
        ExternalStatementCreationDto quizForm = new ExternalStatementCreationDto(tournament);
        Integer quizId = tournamentRequiredService.getQuizId(tournament.getCreator().getId(),
                tournament.getCourseExecution().getId(), quizForm);
        tournament.setQuizId(quizId);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoTournaments() {
        Integer demoCourseExecutionId = tournamentRequiredService.getDemoCourseExecutionId();

        tournamentRepository.getTournamentsForCourseExecution(demoCourseExecutionId)
            .forEach(tournament -> {
                /*tournament.getParticipants().forEach(user ->
                        userRepository.findById(user.getId()).get().removeTournament(tournament)
                );*/
                /*if (tournament.getQuiz() != null) {
                    tournament.getQuiz().setTournament(null);
                }*/
                tournamentRepository.delete(tournament);
            });
    }

}
