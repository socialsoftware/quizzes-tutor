package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;

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
}
