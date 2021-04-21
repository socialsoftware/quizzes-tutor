package pt.ulisboa.tecnico.socialsoftware.tutor.user.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {
    @EntityGraph(attributePaths = {"quizAnswers.questionAnswers"})
    Optional<User> findUserWithQuizAnswersAndQuestionAnswersById(int userId);

    @Query(value = "select * from users u where u.key = :key", nativeQuery = true)
    Optional<User> findByKey(Integer key);

}