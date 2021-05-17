package pt.ulisboa.tecnico.socialsoftware.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUser;

import java.util.Optional;

// Queries need to use default schema for tables
// https://stackoverflow.com/questions/4832579/getting-hibernate-default-schema-name-programmatically-from-session-factory
@Repository
@Transactional
public interface AuthUserRepository extends JpaRepository<AuthUser, Integer> {
    @Query(value = "select * from auth_users u where u.username = lower(:username)", nativeQuery = true)
    Optional<AuthUser> findAuthUserByUsername(String username);

    @Query(value = "select * from auth_users u where u.user_id = :userId", nativeQuery = true)
    Optional<AuthUser> findAuthUserById(Integer userId);

}
