package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface CourseExecutionRepository extends JpaRepository<CourseExecution, Integer> {
    @Query(value = "SELECT * FROM course_executions ce WHERE ce.acronym = :acronym AND ce.academic_term = :academicTerm AND ce.type = :type", nativeQuery = true)
    Optional<CourseExecution> findByAcronymAcademicTermType(String acronym, String academicTerm, String type);
}