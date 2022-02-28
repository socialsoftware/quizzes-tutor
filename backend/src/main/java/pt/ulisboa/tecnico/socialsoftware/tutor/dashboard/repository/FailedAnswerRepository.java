package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.FailedAnswer;

import java.time.LocalDateTime;
import java.util.Set;

@Repository
@Transactional
public interface FailedAnswerRepository extends JpaRepository<FailedAnswer, Integer> {

    @Query(value = "SELECT fa.failed_answer_id FROM failed_answer fa JOIN dashboard da WHERE da.dashboard_id = :dashboardId AND fa.collected > :from AND fa.removed = false", nativeQuery = true)
    Set<Integer> findNewFailedAnswer(int dashboardId, LocalDateTime from);

    @Query(value = "SELECT fa.failed_answer_id FROM failed_answer JOIN dashboard da WHERE da.dashboard_id = :dashboardId AND fa.collected BETWEEN :from AND :until", nativeQuery = true)
    Set<Integer> findFailedAnswerFromDate(int dashboardId, LocalDateTime from, LocalDateTime until);
}

