package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course;

import java.util.Optional;

// Queries need to use default schema for tables
// https://stackoverflow.com/questions/4832579/getting-hibernate-default-schema-name-programmatically-from-session-factory
@Repository
@Transactional
public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query(value = "select * from {h-schema}courses c where c.name = :name and c.type = :type", nativeQuery = true)
    Optional<Course> findByNameType(String name, String type);

    @EntityGraph(attributePaths = {"courseExecutions"})
    Optional<Course> findCourseWithCourseExecutionsById(int courseId);
}