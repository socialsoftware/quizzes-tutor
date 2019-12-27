package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("select u from Users u where u.username = :username")
    User findByUsername(String username);

    @Query("select u from Users u where u.number = :number")
    User findByNumber(Integer number);

    @Query(value = "select MAX(number) from users", nativeQuery = true)
    Integer getMaxUserNumber();

    @Query(value = "select distinct u.year from Users u", nativeQuery = true)
    List<Integer> getCourseYears();

    @Query("select u from Users u where u.year = :year")
    List<User> courseStudents(Integer year);
}