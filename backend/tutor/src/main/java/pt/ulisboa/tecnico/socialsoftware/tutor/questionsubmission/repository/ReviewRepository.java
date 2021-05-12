package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.Review;

import java.util.List;

// Queries need to use default schema for tables
// https://stackoverflow.com/questions/4832579/getting-hibernate-default-schema-name-programmatically-from-session-factory
@Repository
@Transactional
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @Query(value = "SELECT * FROM {h-schema}reviews r WHERE r.question_submission_id = :questionSubmissionId", nativeQuery = true)
    List<Review> findReviewsBySubmissionId(int questionSubmissionId);
}
