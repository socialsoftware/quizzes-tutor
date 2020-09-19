package pt.ulisboa.tecnico.socialsoftware.tutor.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;

import java.util.Optional;

@Repository
@Transactional
public interface AuthUserRepository extends JpaRepository<AuthUser, Integer> {
    @Query(value = "select * from auth_users u where u.username = lower(:username)", nativeQuery = true)
    Optional<AuthUser> findAuthUserByUsername(String username);
}
