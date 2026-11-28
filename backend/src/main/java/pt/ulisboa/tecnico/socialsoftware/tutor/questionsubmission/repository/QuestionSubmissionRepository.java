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
    // Rejected submissions are left out: the student has nothing left to do with them, and the page opens faster
    @Query(value = "select s.* from question_submissions s where s.submitter_id = :submitterId and s.course_execution_id = :courseExecutionId and s.status <> 'REJECTED'", nativeQuery = true)
    List<QuestionSubmission> findQuestionSubmissionsByUserAndCourseExecution(Integer submitterId, Integer courseExecutionId);

    @Query(value = "select s.* from question_submissions s where s.course_execution_id = :courseExecutionId", nativeQuery = true)
    List<QuestionSubmission> findQuestionSubmissionsByCourseExecution(Integer courseExecutionId);

    @Query(value = "select s.* from question_submissions s where s.course_execution_id = :courseExecutionId and s.status in (:statuses)", nativeQuery = true)
    List<QuestionSubmission> findQuestionSubmissionsByCourseExecutionAndStatus(Integer courseExecutionId, java.util.Collection<String> statuses);

    @Query(value = "select s.* from question_submissions s where s.question_id = :questionId", nativeQuery = true)
    QuestionSubmission findQuestionSubmissionByQuestionId(Integer questionId);

    @Query(value = "select question_id from question_submissions s where s.id = :questionSubmissionId", nativeQuery = true)
    Optional<Integer> findQuestionIdByQuestionSubmissionId(Integer questionSubmissionId);

    @Query(value = "select course_execution_id from question_submissions s where s.id = :questionSubmissionId", nativeQuery = true)
    Integer findCourseExecutionIdByQuestionSubmissionId(Integer questionSubmissionId);

    @Query(value = "select submitter_id from question_submissions s where s.id = :questionSubmissionId", nativeQuery = true)
    Integer findSubmitterIdByQuestionSubmissionId(Integer questionSubmissionId);
}
