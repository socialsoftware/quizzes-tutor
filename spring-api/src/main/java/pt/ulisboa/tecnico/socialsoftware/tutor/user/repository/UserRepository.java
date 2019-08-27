package pt.ulisboa.tecnico.socialsoftware.tutor.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("select u from Users u where u.username = :username")
    User findByUsername(String username);

    @Query("select u from Users u where u.number = :number")
    User findByNumber(Integer number);

    @Query(value = "select MAX(number) from users", nativeQuery = true)
    Integer getMaxUserNumber();

}