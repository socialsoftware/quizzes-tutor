package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.WeeklyScore;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface WeeklyScoreRepository extends JpaRepository<WeeklyScore, Integer> {

  @Query(value = "SELECT * FROM weekly_scores ws WHERE ws.dashboard_id = :dashboardId", nativeQuery = true)
  Set<WeeklyScore> findWeeklyScores(int dashboardId);

  @Query(value = "SELECT * FROM weekly_scores ws WHERE ws.week = :week", nativeQuery = true)
  Optional<WeeklyScore> findWeeklyScoreByWeek(LocalDate week);
}
