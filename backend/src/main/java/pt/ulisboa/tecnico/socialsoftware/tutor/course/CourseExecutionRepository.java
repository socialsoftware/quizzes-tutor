package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CourseExecutionRepository extends JpaRepository<CourseExecution, Integer> {

    @Query(value = "SELECT id FROM course_executions ce WHERE ce.acronym = :acronym AND ce.academic_term = :academicTerm", nativeQuery = true)
    int findByAcronymAcademicTerm(String acronym, String academicTerm);
}