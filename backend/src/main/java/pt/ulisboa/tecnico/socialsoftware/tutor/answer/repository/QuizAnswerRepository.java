package pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Integer> {
    @Query(value = "SELECT * FROM quiz_answers qa WHERE qa.user_id = :userId AND qa.quiz_id = :quizId", nativeQuery = true)
    Optional<QuizAnswer> findQuizAnswer(Integer quizId, Integer userId);

    @Query(value = "SELECT * FROM quiz_answers qa JOIN quizzes q ON qa.quiz_id = q.id WHERE (NOT qa.completed AND q.conclusion_date < :now) OR (qa.completed AND NOT qa.used_in_statistics)", nativeQuery = true)
    Set<QuizAnswer> findQuizAnswersToClose(LocalDateTime now);

    @Query(value = "SELECT * FROM quiz_answers qa JOIN quizzes q ON qa.quiz_id = q.id WHERE qa.user_id = :userId AND q.course_execution_id = :executionId", nativeQuery = true)
    Set<QuizAnswer> findQuizAnswers(int userId, int executionId);

    @Query(value = "SELECT * FROM quiz_answers qa JOIN quizzes q ON qa.quiz_id = q.id WHERE qa.user_id = :userId AND q.course_execution_id = :executionId AND (qa.completed OR (q.one_way AND q.creation_date IS NULL))", nativeQuery = true)
    Set<QuizAnswer> findClosedQuizAnswers(int userId, int executionId);

    @Query(value = "SELECT * FROM quiz_answers qa JOIN quizzes q ON qa.quiz_id = q.id WHERE qa.user_id = :userId AND q.course_execution_id = :executionId AND NOT qa.completed AND (q.type = 'GENERATED' OR q.qr_code_only)", nativeQuery = true)
    Set<QuizAnswer> findNotCompletedGeneratedOrQRCodeOnlyQuizAnswers(int userId, int executionId);
}

