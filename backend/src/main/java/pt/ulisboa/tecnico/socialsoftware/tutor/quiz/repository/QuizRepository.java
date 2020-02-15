package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    @Query(value = "SELECT * FROM quizzes q, course_executions c WHERE c.id = q.course_execution_id AND c.id = :executionId", nativeQuery = true)
    List<Quiz> findQuizzes(int executionId);

    @Query(value = "SELECT MAX(key) FROM quizzes", nativeQuery = true)
    Integer getMaxQuizKey();

    @Query(value = "SELECT * FROM quizzes q WHERE q.key = :key", nativeQuery = true)
    Optional<Quiz> findByKey(Integer key);
}