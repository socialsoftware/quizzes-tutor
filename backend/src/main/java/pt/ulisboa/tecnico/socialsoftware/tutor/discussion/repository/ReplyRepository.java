package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;


@Repository
@Transactional
public interface ReplyRepository extends JpaRepository<Reply, Integer> {

    @Query(value = "SELECT r FROM discussions d NATURAL JOIN replies r WHERE d.question_id = :questionId AND r.is_public = true", nativeQuery = true)
    List<Reply> findClarificationsByQuestionId(int questionId);
}
