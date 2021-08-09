package pt.ulisboa.tecnico.socialsoftware.tutor.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = "select * from users u where u.key = :key", nativeQuery = true)
    Optional<User> findByKey(Integer key);

    @Query(value = "select uc.course_executions_id from users_course_executions uc where uc.users_id = :userId", nativeQuery = true)
    Set<Integer> getUserCourseExecutionsIds(int userId);

    @Query(value = "select count(*) from tournaments_participants tp where tp.participants_id = :userId and tp.tournaments_id = :tournamentId", nativeQuery = true)
    Integer countUserTournamentPairById(int userId, int tournamentId);
}