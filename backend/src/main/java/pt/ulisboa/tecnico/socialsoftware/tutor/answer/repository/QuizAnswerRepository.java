package pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Integer> {
    @Query(value = "SELECT * FROM quiz_answers qa WHERE qa.user_id = :userId AND qa.quiz_id = :quizId", nativeQuery = true)
    Optional<QuizAnswer> findQuizAnswer(Integer quizId, Integer userId);

    @Query(value = "SELECT qa.quiz.id FROM QuizAnswer qa JOIN qa.quiz q WHERE qa.student.id = :userId AND q.courseExecution.id = :executionId AND q.availableDate < :now AND (q.conclusionDate IS NULL OR q.conclusionDate > :now) AND qa.completed = true")
    Set<Integer> findClosedQuizAnswersQuizIds(int userId, int executionId, LocalDateTime now);

    @Query(value = "SELECT qa.quiz FROM QuizAnswer qa JOIN qa.quiz q WHERE qa.student.id = :userId AND q.courseExecution.id = :executionId AND q.qrCodeOnly = false AND q.availableDate < :now AND (q.conclusionDate IS NULL OR q.conclusionDate < :now) AND qa.completed = false")
    Set<Quiz> findOpenNonQRCodeQuizAnswers(int userId, int executionId, LocalDateTime now);

    @Query(value = "SELECT qa FROM QuizAnswer qa WHERE qa.quiz.id = :quizId AND qa.creationDate IS NULL")
    Set<QuizAnswer> findNotAnsweredQuizAnswers(int quizId);

    @Query(value = "SELECT qa FROM QuizAnswer qa WHERE qa.quiz.courseExecution.id = :courseExecutionId")
    Set<QuizAnswer>  findByExecutionCourseId(int courseExecutionId);

    @Query(value = "SELECT qa FROM QuizAnswer qa WHERE qa.quiz.courseExecution.id = :courseExecutionId AND qa.student.id = :userId")
    Set<QuizAnswer> findByStudentAndCourseExecution(int userId, int courseExecutionId);

    @Query(value = "SELECT qa FROM QuizAnswer qa WHERE qa.quiz.courseExecution.id = :courseExecutionId AND qa.student.id = :userId AND qa.answerDate >= :start AND qa.answerDate < :end")
    Set<QuizAnswer> findByStudentAndCourseExecutionInPeriod(int userId, int courseExecutionId, LocalDateTime start, LocalDateTime end);
}

