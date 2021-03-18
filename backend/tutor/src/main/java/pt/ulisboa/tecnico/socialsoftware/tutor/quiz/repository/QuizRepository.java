package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository;


import org.springframework.data.jpa.repository.EntityGraph;
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
    @EntityGraph(attributePaths = {"quizAnswers.questionAnswers.quizQuestion.question"})
    Optional<Quiz> findQuizWithAnswersAndQuestionsById(int quizId);

    @Query(value = "SELECT * FROM quizzes q, course_executions c WHERE c.id = q.course_execution_id AND c.id = :executionId ORDER BY c.id", nativeQuery = true)
    List<Quiz> findQuizzesOfExecution(int executionId);

    @Query(value = "SELECT q FROM Quiz q WHERE q.courseExecution.id = :executionId AND q.qrCodeOnly = false AND q.type <> 'GENERATED' AND q.type <> 'TOURNAMENT' AND q.availableDate < :now AND (q.conclusionDate IS NULL OR q.conclusionDate > :now)")
    List<Quiz> findAvailableNonQRCodeNonGeneratedNonTournamentQuizzes(int executionId, LocalDateTime now);

    @Query(value = "SELECT q.course_execution_id FROM quizzes q WHERE q.id = :id", nativeQuery = true)
    Optional<Integer> findCourseExecutionIdById(int id);
}