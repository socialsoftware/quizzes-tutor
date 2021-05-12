package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;

import java.util.List;

// Queries need to use default schema for tables
// https://stackoverflow.com/questions/4832579/getting-hibernate-default-schema-name-programmatically-from-session-factory
@Repository
@Transactional
public interface DiscussionRepository extends JpaRepository<Discussion, Integer> {
    @Query(value = "SELECT * FROM {h-schema}discussions d WHERE d.question_id = :questionId", nativeQuery = true)
    List<Discussion> findDiscussionsByQuestion(Integer questionId);

    @Query(value = "SELECT * FROM {h-schema}discussions d WHERE d.user_id = :userId", nativeQuery = true)
    List<Discussion> findQuestionsByUser(Integer userId);

    @Query(value = "SELECT * FROM {h-schema}discussions d WHERE d.course_execution_id = :courseExecutionId", nativeQuery = true)
    List<Discussion> findDiscussionsByCourseExecution(int courseExecutionId);

    @Query(value = "SELECT * FROM {h-schema}discussions d WHERE d.course_execution_id = :courseExecutionId AND d.user_id = :userId", nativeQuery = true)
    List<Discussion> findDiscussionsByCourseExecutionIdAndUserId(int courseExecutionId, int userId);

    @Query(value = "SELECT * FROM {h-schema}discussions d WHERE d.course_execution_id = :courseExecutionId AND d.closed = false", nativeQuery = true)
    List<Discussion> findOpenDiscussionsByCourseExecutionId(int courseExecutionId);

    @Query(value = "SELECT d FROM Discussion d WHERE d.courseExecution.id = :courseExecutionId")
    List<Discussion> findByExecutionCourseId(int courseExecutionId);
}
