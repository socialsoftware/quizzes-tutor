package pt.ulisboa.tecnico.socialsoftware.apigateway.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.apigateway.auth.domain.AuthUser;

import java.util.Optional;

@Repository
@Transactional
public interface AuthUserRepository extends JpaRepository<AuthUser, Integer> {
    @Query(value = "select * from auth_users u where u.username = lower(:username)", nativeQuery = true)
    Optional<AuthUser> findAuthUserByUsername(String username);

    @Query(value = "select * from auth_users u where u.user_id = :userId", nativeQuery = true)
    Optional<AuthUser> findAuthUserById(Integer userId);

    @Query(value = "select count(*) from authuser_course_executions u where u.auth_user_id = :userId and u.user_course_executions = :courseExecutionId", nativeQuery = true)
    Integer countUserCourseExecutionsPairById(int userId, int courseExecutionId);
}