package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query(value = "select * from courses c where c.name = :name", nativeQuery = true)
    Optional<Course> findByName(String name);
}