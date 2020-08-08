package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface QuestionSubmissionRepository extends JpaRepository<QuestionSubmission, Integer> {
    @Query(value = "select user_id from question_submissions s where s.id = :questionSubmissionId", nativeQuery = true)
    Optional<Integer> findUserIdByQuestionSubmissionId(Integer questionSubmissionId);

    @Query(value = "select * from question_submissions s where s.user_id = :userId and s.course_execution_id = :courseExecutionId", nativeQuery = true)
    List<QuestionSubmission> findQuestionSubmissions(Integer userId, Integer courseExecutionId);

    @Query(value = "select * from question_submissions s where s.course_execution_id = :courseExecutionId", nativeQuery = true)
    List<QuestionSubmission> findCourseExecutionQuestionSubmissions(Integer courseExecutionId);

    @Query(value = "select * from question_submissions s where s.question_id = :questionId", nativeQuery = true)
    QuestionSubmission findQuestionSubmissionByQuestionId(Integer questionId);
}
