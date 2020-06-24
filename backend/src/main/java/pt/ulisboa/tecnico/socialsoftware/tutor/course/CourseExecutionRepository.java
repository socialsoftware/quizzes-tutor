package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface CourseExecutionRepository extends JpaRepository<CourseExecution, Integer> {
    @Query(value = "select * from course_executions ce where ce.acronym = :acronym and ce.academic_term = :academicTerm  and ce.type = :type", nativeQuery = true)
    Optional<CourseExecution> findByFields(String acronym, String academicTerm, String type);
}