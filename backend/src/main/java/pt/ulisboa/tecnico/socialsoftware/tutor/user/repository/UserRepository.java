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

    @Query(value = "select count(*) from users_course_executions uc where uc.users_id = :userId and uc.course_executions_id = :courseExecutionId", nativeQuery = true)
    Integer countUserCourseExecutionsPairById(int userId, int courseExecutionId);

    @Query(value = "select count(*) from tournaments_participants tp where tp.participants_id = :userId and tp.tournaments_id = :tournamentId", nativeQuery = true)
    Integer countUserTournamentPairById(int userId, int tournamentId);
}