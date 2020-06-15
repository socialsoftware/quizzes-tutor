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

    @Query(value = "SELECT * FROM quiz_answers qa JOIN quizzes q ON qa.quiz_id = q.id WHERE qa.completed AND NOT qa.used_in_statistics", nativeQuery = true)
    Set<QuizAnswer> findQuizAnswersToCalculateStatistics(LocalDateTime now);

    @Query(value = "SELECT qa.quiz.id FROM QuizAnswer qa JOIN qa.quiz q WHERE qa.user.id = :userId AND q.courseExecution.id = :executionId AND (qa.completed = true OR (q.type = 'IN_CLASS' AND qa.creationDate IS NOT NULL))")
    Set<Integer> findClosedQuizAnswersQuizIds(int userId, int executionId);

    @Query(value = "SELECT qa FROM QuizAnswer qa JOIN qa.quiz q WHERE qa.user.id = :userId AND q.courseExecution.id = :executionId AND qa.completed = FALSE AND (q.type = 'GENERATED' OR q.qrCodeOnly = true)")
    Set<QuizAnswer> findNotCompletedGeneratedOrQRCodeOnlyQuizAnswers(int userId, int executionId);

    @Query(value = "SELECT qa FROM QuizAnswer qa WHERE qa.quiz.id = :quizId AND qa.creationDate IS NULL")
    Set<QuizAnswer> findNotAnsweredQuizAnswers(int quizId);
}

