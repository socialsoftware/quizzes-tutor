package pt.ulisboa.tecnico.socialsoftware.tournament.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.Tournament;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {
    @Query(value = "SELECT * FROM tournaments t WHERE t.course_execution_id = :courseExecutionId", nativeQuery = true)
    List<Tournament> getTournamentsForCourseExecution(Integer courseExecutionId);

    @Query(value = "SELECT * FROM tournaments t WHERE t.start_time < :now AND t.end_time > :now AND t.is_canceled = 'false' AND t.course_execution_id = :courseExecutionId", nativeQuery = true)
    List<Tournament> getOpenedTournamentsForCourseExecution(Integer courseExecutionId, LocalDateTime now);

    @Query(value = "SELECT * FROM tournaments t WHERE t.end_time < :now AND t.is_canceled = 'false' AND t.course_execution_id = :courseExecutionId", nativeQuery = true)
    List<Tournament> getClosedTournamentsForCourseExecution(Integer courseExecutionId, LocalDateTime now);
    
    @Query(value = "SELECT t.course_execution_id FROM tournaments t WHERE t.id = :id", nativeQuery = true)
    Optional<Integer> findCourseExecutionIdByTournamentId(int id);

    @Query(value = "select count(*) from tournaments_participants tp where tp.participant_id = :userId and tp.tournament_id = :tournamentId", nativeQuery = true)
    Integer countUserTournamentPairById(int userId, int tournamentId);

    @Query(value = "SELECT * FROM tournaments t WHERE t.quiz_id = :quizId", nativeQuery = true)
    Optional<Tournament> findTournamentByQuizId(int quizId);

    @Query(value = "SELECT * FROM tournaments t WHERE t.id = :tournamentId AND t.state = 'APPROVED'", nativeQuery = true)
    Optional<Tournament> findApprovedTournamentById(int tournamentId);
}
