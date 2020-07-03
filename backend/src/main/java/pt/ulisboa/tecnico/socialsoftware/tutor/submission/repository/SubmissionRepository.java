package pt.ulisboa.tecnico.socialsoftware.tutor.submission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain.Submission;

import java.util.List;

@Repository
@Transactional
public interface SubmissionRepository extends JpaRepository<Submission, Integer> {
    @Query(value = "select * from submissions s where s.user_id = :userId and s.course_execution_id = :courseExecutionId", nativeQuery = true)
    List<Submission> getSubmissions(Integer userId, Integer courseExecutionId);

    @Query(value = "select * from submissions s where s.course_execution_id = :courseExecutionId", nativeQuery = true)
    List<Submission> getCourseExecutionSubmissions(Integer courseExecutionId);
}
