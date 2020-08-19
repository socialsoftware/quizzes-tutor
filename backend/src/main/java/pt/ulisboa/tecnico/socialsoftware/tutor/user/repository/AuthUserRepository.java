package pt.ulisboa.tecnico.socialsoftware.tutor.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.AuthUser;

@Repository
@Transactional
public interface AuthUserRepository extends JpaRepository<AuthUser, Integer> {
}
