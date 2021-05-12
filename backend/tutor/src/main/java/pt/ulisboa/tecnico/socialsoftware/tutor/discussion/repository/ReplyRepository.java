package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;

// Queries need to use default schema for tables
// https://stackoverflow.com/questions/4832579/getting-hibernate-default-schema-name-programmatically-from-session-factory
@Repository
@Transactional
public interface ReplyRepository extends JpaRepository<Reply, Integer> {

    @Query(value = "SELECT r.id,r.date,r.is_public,r.message,r.discussion_id,r.user_id FROM {h-schema}discussions d, {h-schema}replies r WHERE r.discussion_id = d.id  AND  d.question_id = :questionId AND r.is_public = true", nativeQuery = true)
    List<Reply> findClarificationsByQuestionId(int questionId);
}
