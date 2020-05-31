package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    @Query(value = "SELECT * FROM quizzes q, course_executions c WHERE c.id = q.course_execution_id AND c.id = :executionId", nativeQuery = true)
    List<Quiz> findQuizzesOfExecution(int executionId);

    @Query(value = "SELECT * FROM quizzes q, course_executions c WHERE c.id = q.course_execution_id AND c.id = :executionId AND q.available_date < :now AND (q.conclusion_date IS NULL OR q.conclusion_date > :now) AND NOT q.type = 'GENERATED' AND NOT q.qr_code_only", nativeQuery = true)
    List<Quiz> findAvailableNonGeneratedNonQRCodeOnlyQuizzes(int executionId, LocalDateTime now);

    @Query(value = "SELECT MAX(key) FROM quizzes", nativeQuery = true)
    Integer getMaxQuizKey();

    @Query(value = "SELECT * FROM quizzes q WHERE q.key = :key", nativeQuery = true)
    Optional<Quiz> findByKey(Integer key);
}