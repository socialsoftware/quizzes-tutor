package pt.ulisboa.tecnico.socialsoftware.tutor.submission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain.Review;

@Repository
@Transactional
public interface ReviewRepository extends JpaRepository<Review, Integer> {
}
