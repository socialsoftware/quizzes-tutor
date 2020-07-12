package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {
    @Query(value = "SELECT * FROM tournaments t WHERE t.course_execution_id IN :set", nativeQuery = true)
    List<Tournament> getAllTournaments(Set set);

    @Query(value = "SELECT * FROM tournaments t WHERE t.start_time < CURRENT_TIMESTAMP AND t.end_time > CURRENT_TIMESTAMP AND t.state = 'NOT_CANCELED' AND t.course_execution_id IN :set", nativeQuery = true)
    List<Tournament> getOpenedTournaments(Set set);

    @Query(value = "SELECT * FROM tournaments t WHERE t.end_time < CURRENT_TIMESTAMP AND t.state = 'NOT_CANCELED' AND t.course_execution_id IN :set", nativeQuery = true)
    List<Tournament> getClosedTournaments(Set set);

    @Query(value = "SELECT * FROM tournaments t WHERE t.user_id = :user_id", nativeQuery = true)
    List<Tournament> getUserTournaments(Integer user_id);

    @Query(value = "SELECT * FROM tournaments t WHERE t.course_execution_id = :execution_id", nativeQuery = true)
    List<Tournament> getCourseExecutionTournaments(Integer execution_id);
}
